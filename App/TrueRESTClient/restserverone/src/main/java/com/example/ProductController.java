package com.example;

import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Route;
import spark.Spark;

/**
 * Created by Felix on 13.05.2016.
 */
public class ProductController
{
    public ProductController(final ProductService productService)
    {
        //leitet die /products Anfrage an den Service weiter und liefert
        //eine Liste der Produktobjekte zurück
        Spark.get("/products", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception
            {
                return productService.getAllProducts();
            }
        },new JsonTransformer());

        Spark.get("/products/:id", (request,response) ->
        {
            String idString = request.params(":id");
            int id = Integer.parseInt(idString);

            Product p = productService.getProduct(id);

            return (p != null ? p : null);
        }, new JsonTransformer());


    }

}
