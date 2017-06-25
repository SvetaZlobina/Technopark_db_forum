
DROP TABLE IF EXISTS userForum;
DROP TABLE IF EXISTS userThread;
DROP TABLE IF EXISTS "user" CASCADE ;
DROP TABLE IF EXISTS forum CASCADE ;
DROP TABLE IF EXISTS thread CASCADE ;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS userThreadVote;
DROP SEQUENCE IF EXISTS post_id_seq ;

CREATE EXTENSION IF NOT EXISTS CITEXT;
CREATE SEQUENCE post_id_seq;

CREATE TABLE "user" (
  id SERIAL PRIMARY KEY,
  nickname CITEXT COLLATE "ucs_basic" UNIQUE NOT NULL,
  fullname VARCHAR(256) NOT NULL,
  about TEXT,
  email CITEXT COLLATE "ucs_basic" UNIQUE NOT NULL
);
CREATE INDEX user_nick_idx ON "user" (nickname);


CREATE TABLE forum (
  id SERIAL PRIMARY KEY,
  title VARCHAR(256),
  "user" CITEXT COLLATE "ucs_basic" NOT NULL REFERENCES "user" (nickname) ON DELETE CASCADE,
  slug CITEXT COLLATE "ucs_basic" NOT NULL UNIQUE,
  posts INTEGER DEFAULT 0,
  threads INTEGER DEFAULT 0
);
CREATE INDEX forum_user_idx ON forum ("user");
CREATE INDEX forum_slug_idx ON forum (slug);


CREATE TABLE thread (
  id SERIAL PRIMARY KEY,
  title VARCHAR(256) ,
  author CITEXT COLLATE "ucs_basic" NOT NULL REFERENCES "user" (nickname) ON DELETE CASCADE,
  forum CITEXT NOT NULL REFERENCES forum (slug) ON DELETE CASCADE,
  message TEXT,
  votes INTEGER NOT NULL DEFAULT 0,
  slug CITEXT UNIQUE,
  created TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX thread_forum_idx ON thread (forum);
CREATE INDEX thread_slug_idx ON thread (slug);


CREATE TABLE post (
  id INTEGER PRIMARY KEY DEFAULT NEXTVAL('post_id_seq'),
  parent INTEGER DEFAULT 0,
  root_thread_marker INTEGER,
  root INTEGER DEFAULT CURRVAL('post_id_seq'),
  author CITEXT COLLATE "ucs_basic",
  message VARCHAR(10000),
  isEdited BOOLEAN DEFAULT FALSE,
  forum CITEXT,
  thread INTEGER,
  created TIMESTAMPTZ DEFAULT now(),
  material_path INTEGER[] DEFAULT '{}'::INTEGER[]
);
CREATE INDEX post_thread_idx ON post (thread);
CREATE INDEX post_parent_idx ON post (parent);
CREATE INDEX post_root_idx ON post (root);
CREATE INDEX parent_tree_idx ON post (root_thread_marker, id);


CREATE TABLE userThreadVote (
  id SERIAL PRIMARY KEY ,
  userID INTEGER,
  threadID INTEGER,
  status INTEGER,
  UNIQUE (userID, threadID)
);
CREATE INDEX userThread_userID_idx ON userThreadVote (userID);
CREATE INDEX userThread_threadID_idx ON userThreadVote (threadID);


CREATE TABLE userForum (
  id SERIAL PRIMARY KEY ,
  user_nickname CITEXT COLLATE "ucs_basic",
  forum_slug CITEXT COLLATE "ucs_basic",
  UNIQUE (user_nickname, forum_slug)
);
CREATE INDEX userForum_user_nickname_idx ON userForum (user_nickname);
CREATE INDEX userForum_forum_slug_idx ON userForum (forum_slug);


CREATE TABLE userThread (
  userID INTEGER PRIMARY KEY REFERENCES "user" (id) ON DELETE CASCADE ,
  threadID INTEGER UNIQUE REFERENCES thread (id) ON DELETE CASCADE
);
CREATE INDEX userThread_idx ON userThread (userID, threadID);



