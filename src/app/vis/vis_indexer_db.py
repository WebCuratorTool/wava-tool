import enum
import sqlite3

from app.vis import vis_tables


class WARCRecordType(enum.Enum):
    request = 1
    response = 2
    metadata = 3


class VisIndexerDB:
    def __init__(self, root_path='/tmp'):
        self.root_path = root_path
        self.conn = sqlite3.connect(f'{root_path}/index.sqlite')
        self.conn.execute(f"CREATE TABLE IF NOT EXISTS {vis_tables.table_urls}")

    def save_url(self, ):
        pass

