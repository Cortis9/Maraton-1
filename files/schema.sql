-- CREATE DATABASE QA_MARATON;
-- use QA_MARATON;
drop table if exists equipo_partida, equipo, persona_equipo, persona, pregunta, tema, historial, partida cascade;
create table tema(tema_id int primary key auto_increment, tema_nombre varchar(200)) collate utf8mb4_spanish2_ci;
create table pregunta(pregunta_id integer primary key auto_increment, pregunta_contenido varchar(500), pregunta_respuesta varchar(500), pregunta_tema int not null, foreign key (pregunta_tema) references tema(tema_id));
create table persona(persona_id integer primary key auto_increment, nombre varchar(200), apellido varchar(200));
create table equipo(equipo_id integer primary key auto_increment, nombre varchar(100), ganadas integer unsigned default 0, perdidas integer unsigned default 0, robadas integer unsigned default 0);
create table persona_equipo(persona_equipo_id integer primary key auto_increment, persona_equipo_equipo integer not null, persona_equipo_persona integer not null, foreign key (persona_equipo_equipo) references equipo(equipo_id), foreign key (persona_equipo_persona) references persona(persona_id));
create table partida( partida_id integer primary key auto_increment, partida_nombre varchar(200) not null, partida_fecha_inicio datetime default NOW(), partida_fecha_terminacion datetime, partida_tiempo_jugado time);
create table equipo_partida( equipo_partida_id integer primary key auto_increment, equipo_partida_equipo integer not null, equipo_partida_partida integer not null, foreign key (equipo_partida_equipo) references equipo(equipo_id), foreign key (equipo_partida_partida) references partida(partida_id));
-- historial_resultado: 0 es fallo, 1 es acerción y 2 es robo
create table historial( historial_id integer primary key auto_increment, historial_equipo integer not null, historial_pregunta integer not null, historial_resultado integer default 0, historial_equipo_robada integer, foreign key (historial_equipo) references equipo_partida(equipo_partida_id), foreign key (historial_pregunta) references pregunta(pregunta_id), foreign key (historial_equipo_robada) references equipo_partida(equipo_partida_id));