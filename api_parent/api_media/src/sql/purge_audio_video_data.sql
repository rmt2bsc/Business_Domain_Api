/**
delete FROM AV_TRACKS;
delete from AV_PROJECT;
delete from AV_ARTIST;

CALL sa_reset_identity('AV_ARTIST', 'DBA', 0);
CALL sa_reset_identity('AV_PROJECT', 'DBA', 0);
CALL sa_reset_identity('AV_TRACKS', 'DBA', 0);

commit;
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
 order by 1