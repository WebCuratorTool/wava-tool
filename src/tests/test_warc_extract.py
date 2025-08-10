from app.vis.vis_indexer import WarcExtractor


def test_extract_files():
    store_path = '/mnt/e/leefr/diagnose/wct'
    rs_num = 1
    warc_extractor = WarcExtractor(f'{store_path}/{rs_num}')

    warc_extractor.extract()

    assert True
