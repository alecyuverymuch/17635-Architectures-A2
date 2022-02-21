
/******************************************************************************************************************
* File:REST.js
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*   1.0 February 2018 - Initial write of assignment 3 for 2018 architectures course(ajl).
*
* Description: This module provides the restful webservices for the Server.js Node server. This module contains GET,
* and POST services.  
*
* Parameters: 
*   router - this is the URL from the client
*   connection - this is the connection to the database
*   md5 - This is the md5 hashing/parser... included by convention, but not really used 
*
* Internal Methods: 
*   router.get("/"... - returns the system version information
*   router.get("/orders"... - returns a listing of everything in the ws_orderinfo database
*   router.get("/orders/:order_id"... - returns the data associated with order_id
*   router.post("/order?"... - adds the new customer data into the ws_orderinfo database
*
* External Dependencies: mysql
*
******************************************************************************************************************/

var mysql = require("mysql");     //Database
const aliveUserStore = require('./AliveUserStore');
const ENDPOINTS = require("./EndpointConfig");

function REST_ROUTER(router, connection, logger) {
    var self = this;
    self.handleRoutes(router, connection, logger);
}

// Here is where we define the routes. Essentially a route is a path taken through the code dependent upon the 
// contents of the URL

REST_ROUTER.prototype.handleRoutes = function (router, connection, logger) {

    // GET with no specifier - returns system version information
    // req paramdter is the request object
    // res parameter is the response object

    router.get("/", function (req, res) {
        res.json({ "Message": "Orders Webservices Server Version 1.0" });
    });

    // GET for /orders specifier - returns all orders currently stored in the database
    // req paramdter is the request object
    // res parameter is the response object
  
    router.get(ENDPOINTS.GET_ORDER_ALL,function(req,res){
        logger.info("Getting all database entries...");
        var query = "SELECT * FROM ??";
        var table = ["orders"];
        query = mysql.format(query, table);
        connection.query(query, function (err, rows) {
            if (err) {
                res.json({ "Error": true, "Message": "Error executing MySQL query" });
            } else {
                res.json({ "Error": false, "Message": "Success", "Orders": rows });
            }
        });
    });

    // GET for /orders/order id specifier - returns the order for the provided order ID
    // req paramdter is the request object
    // res parameter is the response object
     
    router.get(ENDPOINTS.GET_ORDER_BY_ID,function(req,res){
        logger.info("Getting order ID: %s", req.params.order_id);
        var query = "SELECT * FROM ?? WHERE ??=?";
        var table = ["orders", "order_id", req.params.order_id];
        query = mysql.format(query, table);
        connection.query(query, function (err, rows) {
            if (err) {
                logger.error(err);
                res.json({ "Error": true, "Message": "Error executing MySQL query" });
            } else {
                res.json({ "Error": false, "Message": "Success", "Users": rows });
            }
        });
    });

    // POST for /orders?order_date&first_name&last_name&address&phone - adds order
    // req paramdter is the request object - note to get parameters (eg. stuff afer the '?') you must use req.body.param
    // res parameter is the response object 
  
    router.post(ENDPOINTS.POST_ORDER,function(req,res){
        //console.log("url:", req.url);
        //console.log("body:", req.body);
        logger.info("Adding to orders table Order Date: %s, First Name: %s, Last Name: %s, Address: %s,Phone: %s", req.body.order_date, req.body.first_name, req.body.last_name, req.body.address, req.body.phone);
        var query = "INSERT INTO ??(??,??,??,??,??) VALUES (?,?,?,?,?)";
        var table = ["orders", "order_date", "first_name", "last_name", "address", "phone", req.body.order_date, req.body.first_name, req.body.last_name, req.body.address, req.body.phone];
        query = mysql.format(query, table);
        connection.query(query, function (err, rows) {
            if (err) {
                res.json({ "Error": true, "Message": "Error executing MySQL query" });
            } else {
                res.json({ "Error": false, "Message": "User Added !" });
            }
        });
    });

    // DELETE for /orders/order id specifier - deletes the order for the provided order ID
    // req paramdter is the request object
    // res parameter is the response object

    router.delete(ENDPOINTS.DELETE_ORDER_BY_ID,function(req,res){
        console.log("Deleting order ID: ", req.params.order_id);
        var query = "DELETE FROM ?? WHERE ??=?";
        var table = ["orders","order_id",req.params.order_id];
        query = mysql.format(query,table);
        connection.query(query,function(err,rows){
            if(err) {
                res.json({"Error" : true, "Message" : "Error executing MySQL query"});
            } else {
                res.json({"Error" : false});
            }
        });
    });

    router.post(ENDPOINTS.SIGN_UP,function(req,res){
        console.log("Signing the user up");
        var query = "INSERT INTO ??(??,??) VALUES (?,?)";
        var table = ["users","user_name","password",req.body.user_name,req.body.password];
        query = mysql.format(query,table);
        connection.query(query,function(err,rows){
            if(err) {
                console.log(err);
                res.json({"Error" : true, "Message" : "Error executing MySQL query"});
            } else {
                res.json({"Error" : false, "Message" : "User Signed Up !"});
            }
        });
    });

    router.post(ENDPOINTS.SIGN_IN,function(req,res){
        console.log("Signing in user");
        var query = "SELECT * FROM ?? WHERE ??=? AND ??=?";
        var table = ["users","user_name",req.body.user_name,"password",req.body.password];
        query = mysql.format(query,table);
        connection.query(query,function(err,rows){
            if(err) {
                res.json({"Error" : true, "Message" : "Error executing MySQL query"});
            } else {
                /**
                 * Verify...
                 */
                if(rows.length>0){
                    const username = rows[0]['user_name'];
                    aliveUserStore.addAliveUser(username);
                    res.send(username);
                }
                else{
                    res.sendStatus(401);
                }
            }
        });
    });

    router.post(ENDPOINTS.EXIT,function(req,res){
        aliveUserStore.removeAliveUser(req.username);
        res.sendStatus(200);
    });
}

// The next line just makes this module available... think of it as a kind package statement in Java

module.exports = REST_ROUTER;