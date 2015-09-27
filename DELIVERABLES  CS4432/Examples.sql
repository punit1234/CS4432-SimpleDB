/*The SQL commands used for WarmUp and Setup.java
ywen Yuan Wen
Punit Dharani

*/
create table StarcraftUser(id int, username varchar(10), rank varchar(10));

insert into StarcraftUser(id, username, rank) values (1, 'jaedong', 'gm'), (2, 'classic', 'masters'), (3, 'parting', 'gm'),
					(4, 'admiraldooke', 'diamond'), (5, 'huk', 'plat'), (6, 'tlo', 'bronze'),
					(7, 'artosis', 'silver');

create table StarcraftUnit(name varchar(10), damage int);
insert into StarcraftUnit(name, damage) values ('roach', 10), ('hydralisk', 20), ('immortal', 25); 

select name, damage from StarcraftUnit  where damage = 20;

select username , rank from StarcraftUser where rank = 'gm';
