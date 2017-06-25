#!/usr/bin/env bash

psql -f ./sql_scripts/create_tables.sql testdb postgres
#psql -f ./sql_scripts/test.sql testdb postgres