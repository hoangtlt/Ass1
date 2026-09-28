IF OBJECT_ID('dbo.news', 'U') IS NOT NULL DROP TABLE dbo.news;
IF OBJECT_ID('dbo.categories', 'U') IS NOT NULL DROP TABLE dbo.categories;
IF OBJECT_ID('dbo.users', 'U') IS NOT NULL DROP TABLE dbo.users;
GO

CREATE TABLE dbo.users (
    id BIGINT IDENTITY(1,1) NOT NULL,
    username NVARCHAR(50) NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    role INT NOT NULL,
    status INT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_users PRIMARY KEY CLUSTERED (id ASC),
    CONSTRAINT UQ_users_username UNIQUE (username),
    CONSTRAINT CK_users_role CHECK (role IN (1, 2)),
    CONSTRAINT CK_users_status CHECK (status IN (0, 1))
);
GO

CREATE TABLE dbo.categories (
    id BIGINT IDENTITY(1,1) NOT NULL,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500) NULL,
    status INT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_categories PRIMARY KEY CLUSTERED (id ASC),
    CONSTRAINT UQ_categories_name UNIQUE (name),
    CONSTRAINT CK_categories_status CHECK (status IN (0, 1))
);
GO

CREATE TABLE dbo.news (
    id BIGINT IDENTITY(1,1) NOT NULL,
    title NVARCHAR(255) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    category_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    status INT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2 NULL,
    CONSTRAINT PK_news PRIMARY KEY CLUSTERED (id ASC),
    CONSTRAINT CK_news_status CHECK (status IN (0, 1)),
    CONSTRAINT FK_News_Category FOREIGN KEY (category_id) REFERENCES dbo.categories(id) ON DELETE NO ACTION,
    CONSTRAINT FK_News_User FOREIGN KEY (created_by) REFERENCES dbo.users(id) ON DELETE NO ACTION
);
GO

CREATE NONCLUSTERED INDEX IX_news_category_id ON dbo.news(category_id);
CREATE NONCLUSTERED INDEX IX_news_created_by ON dbo.news(created_by);
CREATE NONCLUSTERED INDEX IX_news_title ON dbo.news(title);
GO

