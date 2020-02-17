/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utleon.node.myspa.dao;

import edu.softech.MySpa.modelo.Empleado;
import edu.utleon.node.myspa.utilities.BDConection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author Esau
 */
public class DAOEmpleado
{

    BDConection conexion = new BDConection();

    public Empleado get(long id,
                        String token)
    {
        return null;
    }

    public List<Empleado> getAll(String token)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean save(Empleado e,
                        String token)
    {
        boolean output;

        //comprueba el token
        output = new TokenValidador().validarToken(e.getUsuario().getToken(),
                e.getUsuario().getIdUsuario());
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

            String lastId = "select last_insert_id()";

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
            idPersona = r1.getInt(1);

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

    public void update(Empleado t,
                       String token)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void delete(long id,
                       String token)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
