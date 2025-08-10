from typing import Optional
from urllib.parse import urlparse


def is_absolute(url: Optional[str]):
    if not url:
        return False

    return url.startswith("http://") or url.startswith("https://") or url.startswith("ftp://") or url.startswith(
        "feed://") or url.startswith("mailto:") or url.startswith("mail:") or url.startswith(
        "javascript:") or url.startswith("rtsp://")


def url_to_domain(url: str):
    parsed_url = urlparse(url)
    return parsed_url.hostname

def url_to_top_domain(url:str):
    top_domain='unknown'
    domain=url_to_domain(url)
    



def trim_content_type(content_type: str):
    if not content_type:
        return 'unknown'

    idx = content_type.index(';')
    if idx < 0:
        return content_type

    return content_type[0:idx]
