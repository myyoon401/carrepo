-- 커리포 초기 스키마
-- 사용자와, 가입 시 자동 생성되는 이력서 문서까지.

CREATE TABLE users (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL,
    phone      VARCHAR(20)  NOT NULL,
    email      VARCHAR(255) NOT NULL,
    title      VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL,
    -- 이름·휴대폰·이메일 셋이 한 사람을 식별한다. 식별 의미는 이 제약이 보장하고,
    -- 참조는 대리키(id)가 받는다 — 연락처가 바뀌어도 FK 가 깨지지 않는다.
    CONSTRAINT uk_users_identity UNIQUE (name, phone, email)
);

COMMENT ON COLUMN users.phone IS '숫자만 정규화해 저장한다 (01012345678)';
COMMENT ON COLUMN users.title IS '이력서 부제목. 화면이 보기를 제시하지만 서버는 자유 문자열로 받는다';

CREATE TABLE document (
    id              BIGSERIAL    PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users (id),
    kind            VARCHAR(30)  NOT NULL,
    name            VARCHAR(100) NOT NULL,
    head_version_id BIGINT,
    created_at      TIMESTAMPTZ  NOT NULL,
    updated_at      TIMESTAMPTZ  NOT NULL
);

COMMENT ON COLUMN document.kind IS 'RESUME / PORTFOLIO / CAREER_DESCRIPTION …';
COMMENT ON COLUMN document.head_version_id IS '현재 버전. 버전 insert 후 갱신하므로 nullable 이다';

CREATE INDEX idx_document_user ON document (user_id);

CREATE TABLE document_version (
    id          BIGSERIAL    PRIMARY KEY,
    document_id BIGINT       NOT NULL REFERENCES document (id),
    seq         INT          NOT NULL,
    html        TEXT         NOT NULL,
    change_note VARCHAR(255),
    created_at  TIMESTAMPTZ  NOT NULL,
    -- 버전 행은 불변이다. 수정은 새 seq 를 insert 한다.
    CONSTRAINT uk_document_version_seq UNIQUE (document_id, seq)
);

COMMENT ON TABLE document_version IS '불변. UPDATE 하지 않는다';

ALTER TABLE document
    ADD CONSTRAINT fk_document_head_version
    FOREIGN KEY (head_version_id) REFERENCES document_version (id);
