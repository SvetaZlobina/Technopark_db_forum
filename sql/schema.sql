
DROP TABLE IF EXISTS UserForumLink;
DROP TABLE IF EXISTS UserThreadLink;
DROP TABLE IF EXISTS "User" CASCADE ;
DROP TABLE IF EXISTS Forum CASCADE ;
DROP TABLE IF EXISTS Thread CASCADE ;
DROP TABLE IF EXISTS Post;
DROP TABLE IF EXISTS UserThreadVoteLink;
DROP SEQUENCE IF EXISTS post_id_seq ;

CREATE EXTENSION IF NOT EXISTS citext;
CREATE SEQUENCE post_id_seq;

CREATE TABLE "User" (
  id SERIAL PRIMARY KEY,
  nickname CITEXT COLLATE "ucs_basic" UNIQUE NOT NULL,
  fullname VARCHAR(256) NOT NULL,
  about TEXT,
  email CITEXT COLLATE "ucs_basic" UNIQUE NOT NULL
);
CREATE INDEX user_nick_idx ON "User" (nickname);


CREATE TABLE Forum (
  id SERIAL PRIMARY KEY,
  title VARCHAR(256),
  "user" CITEXT COLLATE "ucs_basic" NOT NULL REFERENCES "User" (nickname) ON DELETE CASCADE,
  slug CITEXT COLLATE "ucs_basic" NOT NULL UNIQUE,
  posts INTEGER DEFAULT 0,
  threads INTEGER DEFAULT 0
);
CREATE INDEX forum_user_idx ON Forum ("user");
CREATE INDEX forum_slug_idx ON Forum (slug);


CREATE TABLE Thread (
  id SERIAL PRIMARY KEY,
  title VARCHAR(256) ,
  author CITEXT COLLATE "ucs_basic" NOT NULL REFERENCES "User" (nickname) ON DELETE CASCADE,
  forum CITEXT NOT NULL REFERENCES Forum (slug) ON DELETE CASCADE,
  message TEXT,
  votes INTEGER NOT NULL DEFAULT 0,
  slug CITEXT UNIQUE,
  created TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX thread_forum_idx ON Thread (forum);
CREATE INDEX thread_slug_idx ON Thread (slug);


CREATE TABLE Post (
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
CREATE INDEX post_thread_idx ON Post (thread);
CREATE INDEX post_parent_idx ON Post (parent);
CREATE INDEX post_root_idx ON Post (root);
CREATE INDEX parent_tree_idx ON Post (root_thread_marker, id);


CREATE TABLE UserThreadVoteLink (
  id SERIAL PRIMARY KEY ,
  userID INTEGER,
  threadID INTEGER,
  status INTEGER,
  UNIQUE (userID, threadID)
);
CREATE INDEX userThread_userID_idx ON UserThreadVoteLink (userID);
CREATE INDEX userThread_threadID_idx ON UserThreadVoteLink (threadID);


CREATE TABLE UserForumLink (
  id SERIAL PRIMARY KEY ,
  user_nickname CITEXT COLLATE "ucs_basic",
  forum_slug CITEXT COLLATE "ucs_basic",
  UNIQUE (user_nickname, forum_slug)
);
CREATE INDEX userForum_user_nickname_idx ON UserForumLink (user_nickname);
CREATE INDEX userForumLink_forum_slug_idx ON UserForumLink (forum_slug);


CREATE TABLE UserThreadLink (
  userID INTEGER PRIMARY KEY REFERENCES "User" (id) ON DELETE CASCADE ,
  threadID INTEGER UNIQUE REFERENCES Thread (id) ON DELETE CASCADE
);
CREATE INDEX userThread_idx ON UserThreadLink (userID, threadID);



