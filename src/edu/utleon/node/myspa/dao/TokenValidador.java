/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utleon.node.myspa.dao;

import edu.softech.MySpa.modelo.Usuario;
import edu.utleon.node.myspa.utilities.BDConection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Esau
 */
public class TokenValidador
{

    BDConection conexion = new BDConection();

    public final boolean validarToken(String token,
                                      long idUsuario)
    {
        boolean output;
        Connection con = conexion.getConexion();
        
        try
        {
            
            String sql = "Select token from usuario where idUsuario=? and token=?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setLong(1, idUsuario);
            stmt.setString(2, token);
            
            ResultSet result = stmt.executeQuery();
            
            if (result.first()) output = true;
            else output = false;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            output = false;
        } 
        finally {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
            }
        }
        return output;
    }
    
    public final boolean iniciarSesionToken(Usuario usuario) throws SQLException
    {
        Connection con = conexion.getConexion();
        
        String sqlSelect = "select idUsuario from usuario where nombreUsuario like ? and password=?";
        PreparedStatement stmtSelect = con.prepareStatement(sqlSelect);
        stmtSelect.setString(1, usuario.getNombreUsuario());
        stmtSelect.setString(2, usuario.getContrasenia());
        ResultSet resultSelect = stmtSelect.executeQuery();
        
        // si no devuelve algún registro, return false
        if(!resultSelect.first())
            return false;
        
        /*
         * Hace permanente el token del usuario
         */
        String sqlUpdate = "update usuario set token = ?";
        PreparedStatement stmtUpdate = con.prepareStatement(sqlUpdate);
        stmtUpdate.setString(1, usuario.getToken());
        
        boolean output = stmtUpdate.execute();
        
        con.close();
        
        return output;
    }
}
