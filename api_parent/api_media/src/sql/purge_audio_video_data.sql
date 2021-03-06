/**
delete FROM AV_TRACKS;
delete from AV_PROJECT;
delete from AV_ARTIST;
CALL sa_reset_identity('AV_ARTIST', 'DBA', 0);
CALL sa_reset_identity('AV_PROJECT', 'DBA', 0);
CALL sa_reset_identity('AV_TRACKS', 'DBA', 0);
commit;

or

CALL "DBA"."sp_refresh_media_tables"()
*/



/**
select name 'artists', count(*) from av_artist group by name having count(*) > 1 
select title 'projects', count(*) from AV_PROJECT group by title having count(*) > 1 
select track_title 'tracks', count(*) from AV_TRACKS group by track_title having count(*) > 1 
*/


select 'artists', count(*) from av_artist
union
select 'projects', count(*) from AV_PROJECT
union
select 'tracks', count(*) from AV_TRACKS
UNION 
select 'movies', count(*) from AV_PROJECT where project_type_id = 2
 order by 1;

// Check genres assigned to audio projects
--select a.ARTIST_ID, a.NAME, p.PROJECT_ID, p.TITLE from av_artist a, av_project p where a.ARTIST_ID = p.ARTIST_ID and  p.genre_id = 999
--select a.ARTIST_ID, a.NAME, p.PROJECT_ID, p.TITLE, t.TRACK_TITLE, t.GENRE_ID from av_artist a, av_project p, AV_TRACKS t where a.ARTIST_ID = p.ARTIST_ID and p.PROJECT_ID = t.PROJECT_ID and  t.genre_id = 999
--select count() from av_project p where genre_id = 999
--select count() from av_tracks p where genre_id = 999

// Delete all movie rows 
--delete from av_project where project_type_id = 2