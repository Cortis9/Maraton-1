package com.bda.db;

import java.sql.*;

public class DB {

    // region singleton

    private static DB instance;

    private static Boolean createdDatabase = false;

    public static DB getInstance(ConnectionData data) {
        if (DB.instance == null)
            new DB(data, false);
        return DB.instance;
    }

    public static DB getInstance() {
        return DB.instance;
    }

    public static Boolean didCreateDatabase() {
        return DB.createdDatabase;
    }
    // endregion

    // region propiedades, getters y setters

    private Connection connection;
    private String url;

    public String getUrl() {
        return url;
    }

    public Connection getConnection() {
        return connection;
    }

    // endregion

    // region constructor

    public DB(ConnectionData data, Boolean createDatabase) {
        // Necesita crear la DB
        if (createDatabase) {
            // Forma el url
            String url = "jdbc:mysql://%s:%d?user=%s";
            url = String.format(url, data.getHost(), data.getPort(), data.getUser());

            // Agrega la contraseña si se agrego
            if (data.getPassword() != null && !data.getPassword().isEmpty())
                url += String.format("&password=%s", data.getPassword());

            // Se intenta conectar con Servidor y crear BD
            try {
                // Abre la conexion
                this.connection = DriverManager.getConnection(url);

                // Crea la base de datos
                Statement statement = this.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                statement.executeUpdate(String.format("CREATE DATABASE %s", data.getDatabase()));
                System.out.println(String.format("Se ha creado correctamente la DB %s", data.getDatabase()));

                // Cerramos la conexion
                this.connection.close();

            } catch (SQLException e) {
                System.out.println("No se pudo conectar con la BD, recuperación fallida!!!");
                e.printStackTrace();
                System.exit(-1);
            }

            // Volvemos a instanciar pero esta vez con una base de datos creada
            DB.createdDatabase = true;
            new DB(data, false);
            return;
        }
        // Conectar con BD
        else {
            // Forma el url
            String url = "jdbc:mysql://%s:%d/%s?user=%s";
            url = String.format(url, data.getHost(), data.getPort(), data.getDatabase(), data.getUser());

            // Agrega la contraseña si se agrego
            if (data.getPassword() != null && !data.getPassword().isEmpty())
                url += String.format("&password=%s", data.getPassword());

            // Se intenta conectar con Servidor y BD
            try {
                // Abre la conexion
                this.connection = DriverManager.getConnection(url);
            } catch (SQLException e) {
                String message = String.format(
                    "No existe la BD %s, creando DB %s!!!", 
                    data.getDatabase(),
                    data.getDatabase()
                );
                System.out.println(message);
                new DB(data, true);
                return;
            }

            // Abre la conexion
            this.url = url;

            if (DB.instance == null)
                DB.instance = this;
            System.out.println("Conexión exitosa a la BD");
        }
    }

    // endregion

    // region metodos estaticos

    /**
     * <p>
     * Pasar a isUpdateable como true si quieres ser capaz de hacerle update a los
     * datos.
     * </p>
     * <p>
     * Para hacerle update a los datos vas a tener:
     * <ol>
     * <li>Usar un método update[el tipo de dato según la API de JDBC] a cada fila
     * del ResultSet obtenido.</li>
     * <li>Luego usar el método updateRow a cada fila del ResultSet obtenido.</li>
     * </ol>
     * </p>
     *
     * @param query        String
     * @param isUpdateable boolean
     * @return ResultSet | null
     */
    public static ResultSet GetOrUpdatetData(String query, boolean isUpdateable) {

        ResultSet resultSet = null;

        try {
            Statement statement = isUpdateable
                    ? DB.getInstance().connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE)
                    : DB.getInstance().connection.createStatement();

            if (isUpdateable)
                statement.executeUpdate(query);
            else
                resultSet = statement.executeQuery(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;

    }

    /**
     * <p>
     * Este método permite ejecutar instrucciones por lotes, de manera que puede
     * poner cualquier cantidad de instrucciones aquí y se van a ejecutar una detrás
     * de otra.
     * </p>
     * <br/>
     * <h3>Este método no devuelve valores por lo que no se recomienda usar
     * instrucciones que devuelvan resultados</h3>
     *
     * @param queries String[]
     */
    public static void ExecuteQueryBATCHING(String[] queries) {
        /**
         * TODO: se le podría agregar algo para que recibiera lotes parametrizados
         * (https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html)
         */
        try {
            if (DB.getInstance() == null)
                throw new SQLException();

            Connection connection = DB.getInstance().getConnection();
            connection.setAutoCommit(false); // Siempre quitar el autocommit antes de ejecutar un lote
            Statement statement = connection.createStatement();

            for (String query : queries)
                statement.addBatch(query);

            // TODO: Podría agregarle una banderas para saber qué pasó en la ejecución del
            // lote de queries
            // int[] updateCounts = statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (BatchUpdateException batchUpdateExcp) {
            // TODO: Se podría mejorar el mensaje para tener más info cuando falle, pero es
            // para otro día
            System.err.println(batchUpdateExcp.getMessage());
        } catch (SQLException sqlExcp) {
            System.err.println(sqlExcp.getMessage());
        }
    }

    /**
     * <p>
     * Este método ejecuta una instrucción Delete, Insert o Update.
     * </p>
     * <br/>
     * <p>
     * Devuelve la cantidad de filas afectadas
     * </p>
     *
     * @param query String
     * @return int
     */
    public static int ExecuteDeleteOrInsertOrUpdate(String query) {

        int affectedRows = 0;

        try {
            if (DB.getInstance() == null)
                throw new SQLException();

            Statement statement = DB.getInstance().connection.createStatement();
            affectedRows = statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectedRows;
    }

    public static void CloseConnection() {
        if (DB.getInstance().connection == null)
            return;
        try {
            DB.getInstance().connection.close();
            DB.instance = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DB.instance = null;
        System.out.println("La conexión a la BD se ha cerrado");
    }

    // endregion
}
