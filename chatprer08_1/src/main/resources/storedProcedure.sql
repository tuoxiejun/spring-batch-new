drop procedure if exists customer_list;

DELIMITER //

CREATE PROCEDURE customer_list(IN in_city VARCHAR(20))
BEGIN
    SELECT * FROM customer
    WHERE city = in_city;

end //

delimiter ;