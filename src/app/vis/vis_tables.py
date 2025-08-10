# bool: 0 or 1 as boolean
table_urls="""index_urls(
id INTEGER PRIMARY KEY AUTOINCREMENT, 
url TEXT UNIQUE,
tot_urls INTEGER,
tot_success INTEGER,
tot_failed INTEGER,
tot_size INTEGER,
outlink_num INTEGER,
domain_id INTEGER,
content_length INTEGER,
content_type TEXT,
status_code INTEGER,
is_seed  INTEGER
)
"""