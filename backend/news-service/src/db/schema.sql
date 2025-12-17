-- =====================================================
-- DATABASE SCHEMA (GIỮ ĐẦY ĐỦ CHỨC NĂNG – THÊM LƯU KHO)
-- Phương án 2: Database + RSS
-- DETAIL + IMAGE + MIGRATION + INDEX + CONSTRAINT + ARCHIVE
-- =====================================================

-- =====================================================
-- BẢNG LƯU TIN TỨC
-- =====================================================
CREATE TABLE IF NOT EXISTS news (
  id SERIAL PRIMARY KEY,

  title TEXT NOT NULL,
  summary TEXT,
  link TEXT UNIQUE NOT NULL,

  published_at TIMESTAMP,
  category VARCHAR(50),
  source VARCHAR(100),
  location VARCHAR(100),

  image_url TEXT,
  content TEXT,
  author TEXT,
  content_type VARCHAR(20),

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- MIGRATION NHẸ – BỔ SUNG CỘT NẾU THIẾU
-- (detail + image đã có; thêm cột archived_at để "đưa vào kho")
-- =====================================================
ALTER TABLE news ADD COLUMN IF NOT EXISTS image_url    TEXT;
ALTER TABLE news ADD COLUMN IF NOT EXISTS content      TEXT;
ALTER TABLE news ADD COLUMN IF NOT EXISTS author       TEXT;
ALTER TABLE news ADD COLUMN IF NOT EXISTS content_type VARCHAR(20);

-- NEW: cột đánh dấu "đã đưa vào kho"
ALTER TABLE news ADD COLUMN IF NOT EXISTS archived_at  TIMESTAMPTZ;

-- =====================================================
-- CONSTRAINT (giữ nguyên, thêm bằng DO $$ để tránh lỗi IF NOT EXISTS)
-- =====================================================
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'chk_news_category'
  ) THEN
    ALTER TABLE news
      ADD CONSTRAINT chk_news_category
      CHECK (category IN ('weather','air','health')) NOT VALID;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'chk_news_content_type'
  ) THEN
    ALTER TABLE news
      ADD CONSTRAINT chk_news_content_type
      CHECK (content_type IN ('html','text')) NOT VALID;
  END IF;
END$$;

-- =====================================================
-- MIGRATE TIMESTAMP → TIMESTAMPTZ (AN TOÀN)
-- =====================================================
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='news' AND column_name='published_at'
      AND data_type='timestamp without time zone'
  ) THEN
    ALTER TABLE news
      ALTER COLUMN published_at TYPE TIMESTAMPTZ
      USING (published_at AT TIME ZONE 'UTC');
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='news' AND column_name='created_at'
      AND data_type='timestamp without time zone'
  ) THEN
    ALTER TABLE news
      ALTER COLUMN created_at TYPE TIMESTAMPTZ
      USING (created_at AT TIME ZONE 'UTC');
  END IF;
END$$;

-- =====================================================
-- BẢNG LỊCH SỬ ĐỌC
-- =====================================================
CREATE TABLE IF NOT EXISTS news_reads (
  id SERIAL PRIMARY KEY,
  news_id INTEGER NOT NULL REFERENCES news(id) ON DELETE CASCADE,
  user_id VARCHAR(100) NOT NULL,
  read_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='news_reads' AND column_name='read_at'
      AND data_type='timestamp without time zone'
  ) THEN
    ALTER TABLE news_reads
      ALTER COLUMN read_at TYPE TIMESTAMPTZ
      USING (read_at AT TIME ZONE 'UTC');
  END IF;
END$$;

-- =====================================================
-- SUBSCRIPTIONS
-- =====================================================
CREATE TABLE IF NOT EXISTS subscriptions (
  id SERIAL PRIMARY KEY,
  user_id VARCHAR(100) NOT NULL,
  location VARCHAR(100),
  category VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='subscriptions' AND column_name='created_at'
      AND data_type='timestamp without time zone'
  ) THEN
    ALTER TABLE subscriptions
      ALTER COLUMN created_at TYPE TIMESTAMPTZ
      USING (created_at AT TIME ZONE 'UTC');
  END IF;
END$$;

-- =====================================================
-- DEVICES
-- =====================================================
CREATE TABLE IF NOT EXISTS devices (
  id SERIAL PRIMARY KEY,
  user_id VARCHAR(100) NOT NULL,
  device_token TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='devices' AND column_name='created_at'
      AND data_type='timestamp without time zone'
  ) THEN
    ALTER TABLE devices
      ALTER COLUMN created_at TYPE TIMESTAMPTZ
      USING (created_at AT TIME ZONE 'UTC');
  END IF;
END$$;

-- =====================================================
-- INDEXES (GIỮ NGUYÊN + BỔ SUNG CHO ARCHIVE & FEED)
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_news_category        ON news (category);
CREATE INDEX IF NOT EXISTS idx_news_location        ON news (location);
CREATE INDEX IF NOT EXISTS idx_news_published_at    ON news (published_at);

CREATE INDEX IF NOT EXISTS idx_news_cat_published_desc
  ON news (category, published_at DESC);

CREATE INDEX IF NOT EXISTS idx_news_loc_published_desc
  ON news (location, published_at DESC);

CREATE INDEX IF NOT EXISTS idx_news_reads_user      ON news_reads (user_id);
CREATE INDEX IF NOT EXISTS idx_news_reads_news_id   ON news_reads (news_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_user   ON subscriptions (user_id);
CREATE INDEX IF NOT EXISTS idx_devices_user         ON devices (user_id);

-- NEW: index giúp lọc nhanh bài đang hiển thị / đã vào kho
CREATE INDEX IF NOT EXISTS idx_news_archived        ON news (archived_at);

-- (Khuyến nghị cho feed top-10): sort nhanh theo bài mới nhất trong nhóm chưa archive
CREATE INDEX IF NOT EXISTS idx_news_active_sort
  ON news (archived_at, published_at DESC, created_at DESC, id DESC);
