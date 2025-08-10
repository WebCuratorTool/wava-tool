from dataclasses import dataclass
from typing import Optional


def is_success(status_code: int):
    return 200 <= status_code < 400


@dataclass
class NodeBasic:
    id: int
    url: str
    parentId: Optional[int] = None
    totUrls: Optional[int] = None
    totSuccess: Optional[int] = None
    totFailed: Optional[int] = None
    totSize: Optional[int] = None
    outlinkNum: Optional[int] = None
    domainId: Optional[int] = None
    contentLength: Optional[int] = None
    contentType: Optional[str] = None
    statusCode: Optional[int] = None
    isSeed: Optional[int] = None

    def set_content_type(self, content_type: Optional[str]):
        if not content_type:
            self.contentType = 'unknown'
            return

        idx_semicolon = content_type.index(';')
        if idx_semicolon > 0:
            self.contentType = content_type[0:idx_semicolon]
        else:
            self.contentType = content_type

    def _increase_tot_success(self, tot_success):
        self.totSuccess += tot_success
        self.totUrls += tot_success

    def _increase_tot_failed(self, tot_failed):
        self.totFailed += tot_failed
        self.totUrls += tot_failed

    def _increase_tot_size(self, tot_size):
        self.totSize += tot_size

    def _accumulate(self, status_code, content_length):
        self._increase_tot_size(content_length)
        if is_success(status_code):
            self._increase_tot_success(1)
        else:
            self._increase_tot_failed(1)

    def accumulate(self):
        self._accumulate(self.statusCode, self.contentLength)

    def accumulate_node(self, node):
        self._increase_tot_size(node.totSize)
        self._increase_tot_success(node.totSuccess)
        self._increase_tot_failed(node.totFailed)

    def set_zero(self):
        self.totSize = 0
        self.totUrls = 0
        self.totSuccess = 0
        self.totFailed = 0


@dataclass
class NodeUrl(NodeBasic):
    parse_flag_request: bool = None
    parse_flag_response: bool = None
    parse_flag_metadata: bool = None
    viaUrl: str = None
    domain: str = None
    topDomain: str = None
    offset: int = None
    fetchTimeMs: int = None
    fileName: str = None
    outlinks = []
