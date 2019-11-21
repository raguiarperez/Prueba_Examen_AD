package exa15;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


public class Exa15 {
    public static Connection conexion=null;

    public static Connection getConexion() throws SQLException  {
        String usuario = "hr";
        String password = "hr";
        String host = "localhost"; 
        String puerto = "1521";
        String sid = "orcl";
        String ulrjdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;
        
           
            conexion = DriverManager.getConnection(ulrjdbc);
            return conexion;
        }

     
    public static void closeConexion() throws SQLException {
      conexion.close();
      }
 
    public static int ConsultaComposicion(String Codp) throws SQLException{
        Connection con = conexion;
        String consulta="select * from composicion where codp=?";
        PreparedStatement stm = conexion.prepareStatement(consulta);          
        stm.setString(1, Codp);
        ResultSet rs=stm.executeQuery();
                        
        int grasa=0; // inizializamos variables grasa en Integer
        int grasaTemp=0;
                        
        while (rs.next()) {
         //si la consulta contiene datos:
            String codigo = rs.getString("codc");  //cogemos como un String el codc
            int peso = rs.getInt("peso"); //cogemos como un Integer el peso
                                //realizamos el calculo de la grasa:
                                int porcentaje = consultaComponentes(codigo);
                                double porcentajeTemp = (double)porcentaje/100;
                                grasaTemp =(int)((int) peso * porcentajeTemp);           
                                grasa = grasa + grasaTemp;
                        }
                     return grasa;
          
            }
            public static int consultaComponentes(String cod) throws SQLException{
                Connection con = conexion;
                String consulta = "select * from componentes where codc=?"; //seleccionamos los componentes segun el codc de la tabla composición
                PreparedStatement stmt= con.prepareStatement(consulta);
                stmt.setString(1,cod);
                ResultSet rs = stmt.executeQuery(); //ejecutamos la consulta
        
                int grasa = 0;
                while (rs.next()) {
                    grasa = rs.getInt("graxa");
                }
                return grasa;
            }
             public static void main(String[] args) throws FileNotFoundException, IOException, SQLException, ClassNotFoundException, XMLStreamException {
       
            FileInputStream fis = new FileInputStream("/home/dam2/Escritorio/Archivos_AD/Prueba Examen/PruebaExamen/src/exa15/platoss");
            ObjectInputStream leer = new ObjectInputStream(fis);
            Platos platito= new Platos(); // creamos nuevo objeto plato
            platito=(Platos)leer.readObject();//leemos el objeto
            while(platito!=null){ //Si platito no es nulo:
                
                platito.getCodigop(); // cogemos codigo
                platito.getNomep(); // cogemos nombre
                System.out.println(platito.toString()); // lo mostrampos
                platito=(Platos)leer.readObject();   // vuelve a leer
                
                /*******************************************************/
                 
        XMLOutputFactory a4 = XMLOutputFactory.newInstance();
        XMLStreamWriter a5 = a4.createXMLStreamWriter(new  FileWriter (new File ("Platos.xml")));
        a5.writeStartDocument("1.0");
        a5.writeStartElement("PLatos");
 
        getConexion();
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("platos2"));
       
        // Se lee el primer objeto
        Platos aux = (Platos)ois.readObject();
        // Mientras haya objetos
        while (aux!=null){
        if (aux instanceof Platos)
        a5.writeStartElement("Plato");
        System.out.println(aux.toString());
        String cod = aux.getCodigop();
        int grasa = ConsultaComposicion(cod);
        System.out.println(grasa);
        String grasaString = Integer.toString(grasa);
        a5.writeAttribute("Codigop", aux.getCodigop());
        a5.writeStartElement("nomep");
        a5.writeCharacters(aux.getNomep());
        a5.writeEndElement();
        a5.writeStartElement("graxatotal");
        a5.writeCharacters(grasaString);
        a5.writeEndElement();
        a5.writeEndElement();
        aux =(Platos)ois.readObject();
        }
        a5.writeEndElement();
        ois.close();
        closeConexion();
        a5.close();
        
        
        
        
        
    } 
             }
        
}



   