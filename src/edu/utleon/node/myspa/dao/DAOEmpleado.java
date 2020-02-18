/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utleon.node.myspa.dao;

import edu.softech.MySpa.modelo.Empleado;
import edu.softech.MySpa.modelo.Usuario;
import edu.utleon.node.myspa.utilities.BDConection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Esau
 */
public class DAOEmpleado
{

    BDConection conexion = new BDConection();

    /**
     * No funciona porque no tiene utilidad
     *
     * @param id
     * @param token
     * @return
     */
    public Empleado get(String numeroEmpleado,
                        String token)
    {
        return null;
    }

    /**
     * Método de para obtener el listado de todos los empleado Recibe las
     * licencias de Usuario (token, idUsuario)
     *
     * @param token
     * @param idUsuario
     * @return
     */
    public List<Empleado> getAll(String token)
    {
        List<Empleado> empleados = new ArrayList<>();
        Connection con = conexion.getConexion();

        boolean revisionUsuario;
        // comprueba token
        revisionUsuario = new TokenValidador().validarToken(token);
        if (!revisionUsuario)
            return null;

        String sql = "select * from vw_empleado";

        PreparedStatement stmt;
        ResultSet result;

        try
        {
            con.setAutoCommit(false);

            stmt = con.prepareStatement(sql);
            result = stmt.executeQuery();

            while (result.next())
            {
                Empleado e = new Empleado();

                e.setIdEmpleado(result.getInt("idEmpleado"));
                e.setNumeroEmpleado(result.getString("numeroEmpleado"));
                e.setPuesto(result.getString("puesto"));
                e.setEstatus(result.getInt("estatus"));
                e.setFoto(result.getString("foto"));

                e.setIdPersona(result.getInt("idPersona"));
                e.setNombre(result.getString("nombre"));
                e.setApellidoPaterno(result.getString("apellidoPaterno"));
                e.setApellidoMaterno(result.getString("apellidoMaterno"));
                e.setGenero(result.getString("genero"));
                e.setDomicilio(result.getString("domicilio"));
                e.setTelefono(result.getString("telefono"));
                e.setRfc(result.getString("rfc"));

                Usuario u = new Usuario();
                u.setIdUsuario(result.getInt("idUsuario"));
                u.setNombreUsuario(result.getString("nombreUsuario"));
                // u.setContrasenia(result.getString("contrasenia"));
                u.setRol(result.getString("rol"));

                // agrega el usuario a empleado
                e.setUsuario(u);

                // agrega el empleado a la lista
                empleados.add(e);
            }
        }
        catch (SQLException sqle1)
        {
            sqle1.printStackTrace();
            try
            {
                con.close();
            }
            catch (Exception sqle2)
            {
                sqle2.printStackTrace();
            }
        }
        finally
        {
            try
            {
                con.setAutoCommit(true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return empleados;
    }

    public boolean save(Empleado e,
                        String token)
    {
        boolean output;

        //comprueba el token
        output = new TokenValidador().validarToken(token);
        if (!output)
            return false;

        // inicia proceso de ejecutar transacciones
        Connection con = conexion.getConexion();

        int idPersona;
        int idUsuario;
        int idEmpleado;

        try
        {
            con.setAutoCommit(false);

            String lastId = "select last_insert_id() as id";

            // statements for last_insert_id
            PreparedStatement psl1 = con.prepareStatement(lastId);
            PreparedStatement psl2 = con.prepareStatement(lastId);
            PreparedStatement psl3 = con.prepareStatement(lastId);
            // resultset de last_insert_id
            ResultSet r1;
            ResultSet r2;
            ResultSet r3;

            // persona
            String sql1 = "insert into persona(nombre,"
                    + "apellidoPaterno,"
                    + "apellidoMaterno,"
                    + "genero,"
                    + "domicilio,"
                    + "telefono,"
                    + "rfc) values(?,?,?,?,?,?,?)";
            PreparedStatement ps1 = con.prepareStatement(sql1);
            ps1.setString(1, e.getNombre());
            ps1.setString(2, e.getApellidoPaterno());
            ps1.setString(3, e.getApellidoMaterno());
            ps1.setString(4, e.getGenero());
            ps1.setString(5, e.getDomicilio());
            ps1.setString(6, e.getTelefono());
            ps1.setString(7, e.getRfc());

            ps1.executeUpdate();
            
            
            r1 = psl1.executeQuery();
            idPersona = r1.getInt("id");

            // usuario
            String sql2 = "insert into usuario(nombreUsuario,"
                    + "contrasenia,"
                    + "rol) values(?,?,?)";
            PreparedStatement ps2 = con.prepareStatement(sql2);
            ps2.setString(1, e.getUsuario().getNombreUsuario());
            ps2.setString(2, e.getUsuario().getContrasenia());
            ps2.setString(3, e.getUsuario().getRol());

            ps2.executeUpdate();
            r2 = psl2.executeQuery();
            idUsuario = r2.getInt(1);

            // empleado
            String sql3 = "insert into empleado(puesto,"
                    + "foto,"
                    + "idPersona,"
                    + "idUsuario) values(?,?,?,?)";
            PreparedStatement ps3 = con.prepareStatement(sql3);
            ps3.setString(1, e.getPuesto());
            ps3.setString(2, e.getFoto());
            ps3.setInt(3, idPersona);
            ps3.setInt(4, idUsuario);

            ps3.executeUpdate();
            r3 = psl3.executeQuery();
            idEmpleado = r3.getInt(1);

            con.commit();
            output = true;
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            output = false;
            try
            {
                con.rollback();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        finally
        {
            try
            {
                con.setAutoCommit(true);
                con.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return output;
    }

    public boolean update(Empleado empleado,
                          String token)
    {
        // variable que valida la sesión y se hicieron las actualizaciones
        boolean output;

        //comprueba el token
        output = new TokenValidador().validarToken(token);
        if (!output)
            return false;

        // inicia proceso de ejecutar transacciones
        Connection con = conexion.getConexion();

        try
        {
            con.setAutoCommit(false);

            String sqlPersona = "update Persona set "
                    + "nombre = ?, "
                    + "apellidoPaterno = ?, "
                    + "apellidoMaterno = ?, "
                    + "genero = ?, "
                    + "domiclio = ?, "
                    + "telefono = ?,"
                    + "rfc = ? "
                    + "where idPersona = ?";

            String sqlUsuario = "update Usuario set "
                    + "rol = ?"
                    + "where idUsuario=?";

            String sqlEmpleado = "update Empleado set "
                    + "puesto = ?, "
                    + "foto = ? "
                    + "where numeroEmpleado=?";

            // statements for last_insert_id
            PreparedStatement ps1 = con.prepareStatement(sqlPersona);
            PreparedStatement ps2 = con.prepareStatement(sqlUsuario);
            PreparedStatement ps3 = con.prepareStatement(sqlEmpleado);

            // set de parámetros anónimos en el sql para Persona
            ps1.setString(1, empleado.getNombre());
            ps1.setString(2, empleado.getApellidoPaterno());
            ps1.setString(3, empleado.getApellidoMaterno());
            ps1.setString(4, empleado.getGenero());
            ps1.setString(5, empleado.getDomicilio());
            ps1.setString(6, empleado.getTelefono());
            ps1.setString(7, empleado.getRfc());
            // referencia a la persona que se intenta actualizar
            ps1.setString(8, empleado.getNumeroEmpleado());

            // set de parámetros anónimos para el sql de Usuario
            ps2.setString(1, empleado.getUsuario().getRol());
            // referencia al usuario que se intenta actualizar
            ps2.setInt(2, empleado.getUsuario().getIdUsuario());

            // set de parámetros anónimos para el sql de Empleado
            ps3.setString(1, empleado.getPuesto());
            ps3.setString(2, empleado.getFoto());
            // referencia del empleado que se intenta actualizar
            ps3.setInt(3, empleado.getIdEmpleado());

            ps1.execute();
            ps2.execute();
            ps3.execute();

            con.commit();;
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            output = false;
            try
            {
                con.rollback();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        finally
        {
            try
            {
                con.setAutoCommit(true);
                con.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return output;
    }

    public boolean delete(String numeroEmpleado,
                          String token)
    {
        boolean output;

        //comprueba el token
        output = new TokenValidador().validarToken(token);
        if (!output)
            return false;

        Connection con = conexion.getConexion();

        try
        {
            con.setAutoCommit(false);
            
            String sqlEliminar = "update Persona set estatus=0 where numeroEmpleado=?";
            
            PreparedStatement stmt = con.prepareStatement(sqlEliminar);
            
            stmt.execute();

            con.commit();
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            output = false;
            try
            {
                con.rollback();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        finally
        {
            try
            {
                con.setAutoCommit(true);
                con.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return output;
    }

    public static void main(String[] args)
    {
        DAOEmpleado dao = new DAOEmpleado();

        Empleado empleado = new Empleado();
        
        empleado.setNombre("esau");
        empleado.setApellidoPaterno("mtz");
        empleado.setApellidoMaterno("lpz");
        empleado.setGenero("h");
        empleado.setDomicilio("domic");
        empleado.setTelefono("");
        empleado.setRfc("");
        
        
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("user");
        usuario.setRol("admin");
        
        empleado.setUsuario(usuario);
        
        empleado.setPuesto("puesto");
        empleado.setFoto("");
        
        
        dao.save(empleado, "1");
    }
}
