from app.utils.vis_domain_manager import DomainManager


def test_get_domain():
    domain_mgmt = DomainManager()

    url = 'https://drive.google.com/drive/home'
    domain = domain_mgmt.get_domain(url)
    print(f'domain={domain}, url={url}')
    assert domain == 'drive.google.com'

    url = 'https://github.com/WebCuratorTool/wava-tool/blob/main/README.md'
    domain = domain_mgmt.get_domain(url)
    print(f'domain={domain}, url={url}')
    assert domain == 'github.com'


def test_get_top_domain():
    domain_mgmt = DomainManager()

    url = 'https://drive.google.com/drive/home'
    top_domain = domain_mgmt.get_top_domain_from_url(url)
    print(f'domain={top_domain}, url={url}')
    assert top_domain == 'google.com'

    url = 'https://github.com/WebCuratorTool/wava-tool/blob/main/README.md'
    top_domain = domain_mgmt.get_top_domain_from_url(url)
    print(f'domain={top_domain}, url={url}')
    assert top_domain == 'github.com'
