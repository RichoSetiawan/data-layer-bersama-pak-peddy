create table belajar.produk(
	id SERIAL primary key,
	nama TEXT,
	harga numeric	
);

create table belajar.log_harga(
id SERIAL primary key,
produk_id int,
harga_lama numeric,
harga_baru numeric,
waktu timestamp
);

create or replace function log_perubahan_harga()
returns trigger as $$
begin 
	if new.harga <> old.harga then 
	insert into belajar.log_harga (produk_id, harga_lama, harga_baru, waktu)
	values (old.id, old.harga, new.harga, now());
end if;
return new;
end;
$$ language plpgsql;

create trigger triger_log_harga
after update on produk
for each row
execute function log_perubahan_harga();


insert into produk (nama, harga) values ('Espresso', 20000), ('Latte', 25000);

UPDATE belajar.produk
SET nama='Matcha Latte', harga=40000
WHERE id=2;

select * from information_schema.triggers;