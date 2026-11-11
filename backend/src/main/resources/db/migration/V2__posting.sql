-- 공고. 외부 채용 사이트에서 가져오거나 붙여넣은 원문을 그대로 보관한다.

CREATE TABLE company (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uk_company_name UNIQUE (name)
);

COMMENT ON TABLE company IS '회사를 행으로 둔 이유: 재지원·계열 집계가 문자열 매칭이 되면 추적이 깨진다';

CREATE TABLE posting (
    id          BIGSERIAL    PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users (id),
    company_id  BIGINT       NOT NULL REFERENCES company (id),
    title       VARCHAR(200) NOT NULL,
    source_url  VARCHAR(2048),
    deadline_at DATE,
    raw_md      TEXT         NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL
);

COMMENT ON COLUMN posting.source_url IS '출처. 붙여넣기만 한 경우 비어 있을 수 있다';
COMMENT ON COLUMN posting.raw_md IS '공고 원문. 추출 결과든 붙여넣은 글이든 손대지 않고 보관한다';

CREATE INDEX idx_posting_user_created ON posting (user_id, created_at DESC);
CREATE INDEX idx_posting_company ON posting (company_id);
