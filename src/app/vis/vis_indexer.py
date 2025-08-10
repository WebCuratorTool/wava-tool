import glob
import logging
import threading
from pathlib import Path

from warcio import ArchiveIterator
from warcio.recordloader import ArcWarcRecord

from app.utils.vis_domain_manager import DomainManager
from app.vis.vis_models import NodeUrl


class WarcExtractor:
    def __init__(self, root_path):
        self.domain_mgmt = DomainManager()
        self.root_path = Path(root_path)
        self.urls = {}
        self.max_id = 0
        self.running = threading.Event()

    def close(self):
        self.running.set()

    @property
    def next_id(self):
        self.max_id += 1
        return self.max_id

    def extract(self):
        if self.root_path.is_file():
            self._extract(self.root_path)
        else:
            for file_path in glob.glob(f'{self.root_path}/*.*'):
                self._extract(Path(file_path))

    def _extract(self, file_path: Path):
        if file_path.suffix not in ('.warc', '.WARC', '.warc.gz', '.WARC.GZ'):
            # raise RuntimeError(f'Unknown file type: {file_path}')
            return

        with open(str(file_path), 'rb') as stream:
            for record in ArchiveIterator(stream):
                self.step_record(file_path.name, record)

    def step_record(self, file_name: str, record: ArcWarcRecord):
        rec_type = record.rec_type
        if not rec_type or rec_type not in ('response', 'request', 'metadata'):
            logging.debug(f'skip rec_type: {rec_type}')
            return

        warc_headers = dict(record.rec_headers.headers)
        mime = warc_headers.get('Content-Type', '')
        if not mime or mime in ('text/dns',):
            logging.debug(f'skip mime: {mime}')
            return

        url = warc_headers.get('WARC-Target-URI', '')
        if not url:
            logging.debug(f'skip empty url')
            return

        if url in self.urls:
            res = self.urls.get(url)
        else:
            res = NodeUrl(id=self.next_id, url=url)
            res.fileName = file_name
            res.domain = self.domain_mgmt.get_domain(url)
            res.topDomain = self.domain_mgmt.get_top_domain(res.domain)
            self.urls[url] = res

        http_headers = dict(record.http_headers.headers) if record.http_headers else {}
        if rec_type == 'request':
            res.parse_flag_request = True
            referer = http_headers.get('Referer', '')
            if referer:
                print(f'{url} --> {referer}')
            if res.parse_flag_metadata and res.isSeed and referer:
                res.isSeed = True
                res.viaUrl = referer
            elif referer and not res.parse_flag_metadata:
                res.viaUrl = referer
        elif rec_type == 'response':
            res.parse_flag_response = True
            
        elif rec_type == 'metadata':
            res.parse_flag_metadata = True
