package com.bda.db;

import java.sql.*;

public class DB {

    // region singleton

    private static DB instance;

    public static DB getInstance(String host, int port, String database, String user, String password) {
        if (DB.instance == null)
            DB.instance = new DB(host, port, database, user, password);
        return DB.instance;
    }

    public static DB getInstance() {
        return DB.instance;
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

    public DB(String host, int port, String database, String user, String password) {

        String url = new String();

        try {
            url = String.format(
                    "jdbc:mysql://%s:%d/%s?user=%s",
                    host,
                    port,
                    database,
                    user
            );

            if (password != null && !password.isEmpty())
                url += String.format("&password=%s", password);

            this.connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            this.url = url;

            if (DB.instance != null)
                DB.instance = this;
        }
    }

    // endregion

    // region metodos estaticos

    /**
     * <p>
     * Pasar a isUpdateable como true si quieres ser capaz de hacerle update a los datos.
     * </p>
     * <p>
     * Para hacerle update a los datos vas a tener:
     *     <ol>
     *         <li>
     *            Usar un método update[el tipo de dato según la API de JDBC] a cada fila del ResultSet obtenido.
     *         </li>
     *         <li>
     *             Luego usar el método updateRow a cada fila del ResultSet obtenido.
     *         </li>
     *     </ol>
     * </p>
     *
     * @param query        String
     * @param isUpdateable boolean
     * @return ResultSet | null
     */
    public static ResultSet GetOrUpdatetData(String query, boolean isUpdateable) {

        ResultSet resultSet = null;

        try {
            if (DB.getInstance() == null) throw new SQLException();

            Statement statement = isUpdateable ?
                    DB.getInstance().connection.createStatement(
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ) :
                    DB.getInstance().connection.createStatement();

            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return resultSet;
        }
    }

    /**
     * <p>
     * Este método permite ejecutar instrucciones por lotes, de manera que puede poner cualquier cantidad de instrucciones
     * aquí y se van a ejecutar una detrás de otra.
     * </p>
     * <br/>
     * <h3>Este método no devuelve valores por lo que no se recomienda usar instrucciones que devuelvan resultados</h3>
     *
     * @param queries String[]
     */
    public static void ExecuteQueryBATCHING(String[] queries) {
        /**
         *  TODO: se le podría agregar algo para que recibiera lotes para metrizados
         *      (https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html)
         */
        try {
            if (DB.getInstance() == null) throw new SQLException();

            Connection connection = DB.getInstance().getConnection();
            connection.setAutoCommit(false); // Siempre quitar el autocommit antes de ejecutar un lote
            Statement statement = connection.createStatement();

            for (String query : queries)
                statement.addBatch(query);

            // TODO: Podría agregarle una banderas para saber qué pasó en la ejecución del lote de queries
            int[] updateCounts = statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (BatchUpdateException batchUpdateExcp) {
            // TODO: Se podría mejorar el mensaje para tener más info cuando falle, pero es para otro día
            System.err.println(batchUpdateExcp.getMessage());
        } catch (SQLException sqlExcp) {
            System.err.println(sqlExcp.getMessage());
        }
    }

    /**
     * <p>Este método ejecuta una instrucción Delete, Insert o Update.</p>
     * <br/>
     * <p>Devuelve la cantidad de filas afectadas</p>
     *
     * @param query String
     * @return int
     */
    public static int ExecuteDeleteOrInsertOrUpdate(String query) {

        int affectedRows = 0;

        try {
            if (DB.getInstance() == null) throw new SQLException();

            Statement statement = DB.getInstance().connection.createStatement();
            affectedRows = statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectedRows;
    }

    public static void CloseConnection(){
        if(DB.getInstance().connection == null) return;
        try {
            DB.getInstance().connection.close();
            DB.instance = null;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    // endregion
}
