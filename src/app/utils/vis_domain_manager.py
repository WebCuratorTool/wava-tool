from pathlib import Path
from urllib.parse import urlparse


class DomainSuffixNode:
    def __init__(self):
        self.next: dict[str, DomainSuffixNode] = {}

    def get_next_node(self, key: str):
        return self.next.get(key, None)

    def put_next_node(self, key: str):
        if key not in self.next:
            self.next[key] = DomainSuffixNode()

    def contains_key(self, key):
        return key in self.next


class DomainManager:
    def __init__(self):
        self.root = DomainSuffixNode()
        self.load_domain_suffix()

    def load_domain_suffix(self):
        current_dir = Path(__file__).resolve().parent
        data_file = current_dir.joinpath('public_suffix_list.dat')
        with open(str(data_file), 'r+t') as fp:
            for line in fp.readlines():
                if not line:
                    continue
                line = line.strip()
                if len(line) == 0:
                    continue
                if line.startswith('//'):
                    continue
                self.insert(line)

    def insert(self, line: str):
        items = line.split('.')
        p = self.root
        for key in reversed(items):
            p.put_next_node(key)
            p = p.get_next_node(key)

    @staticmethod
    def get_domain(url: str):
        parsed_url = urlparse(url)
        return parsed_url.hostname

    def get_top_domain_from_url(self, url):
        domain = self.get_domain(url)
        return self.get_top_domain(domain)

    def get_top_domain(self, domain):
        if not domain:
            return 'unknown'
        items = domain.split('.')
        p = self.root
        ret = []
        i = len(items) - 1
        for key in reversed(items):
            if not p or not p.contains_key(key):
                break
            ret.append(key)
            p = p.get_next_node(key)
            i -= 1

        if i >= 0:
            ret.append(items[i])

        return '.'.join(reversed(ret))
