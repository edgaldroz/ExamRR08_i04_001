package com.exam.examrr08_i04_001;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.exam.examrr08_i04_001.adaptadores.adapterProductos;
import com.exam.examrr08_i04_001.controlador.controllerAlumno;
import com.exam.examrr08_i04_001.modelo.Alumno;

import java.util.ArrayList;
import java.util.List;

import static com.exam.examrr08_i04_001.R.id.Listado_Alumnos;

public class Principal extends AppCompatActivity {

    private EditText txtCodigo;
    private EditText txtNombre;
    private EditText txtEdad;
    private Button btnGuardar,btnBuscar;
    private Alumno Modelo;

    private ListView Listado_ALumnos;
    private ArrayList<Alumno> Alumnos;
    private controllerAlumno Controlador;
    private adapterProductos adaptadorAlumno;



    public void onResume(){
        super.onResume();
        this.Consultar_Alumnos();
        this.adaptadorAlumno = new adapterProductos(this,R.layout.plantillaalumno, this.Alumnos);
        this.Listado_ALumnos.setAdapter(this.adaptadorAlumno);
    }

    private void Consultar_Alumnos()
    {
        try {
            //Abrimos la conexion
            this.Controlador.Abrir();
            Cursor tabla = this.Controlador.Consultar_Todos();

            //Validar que no este vacio
            if (tabla != null && tabla.getCount() > 0){
                //Recorremos los elementos de la tabla
                tabla.moveToFirst();
                //this.Productos.clear();
                this.Alumnos = new ArrayList<>();

                do
                {
                    Alumno Fila = new Alumno();
                    Fila.setCodigo(tabla.getString(0));
                    Fila.setNombre(tabla.getString(1));
                    Fila.setEdad(tabla.getInt(2));

                    this.Alumnos.add(Fila);
                }while (tabla.moveToNext());
            }

            //Cerramos la conexion
            this.Controlador.Cerrar();

        }catch (SQLiteException ex)
        {
            Toast.makeText(this, "Error al obtener los Alumnos de la base de datos", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        this.Controlador = new controllerAlumno(this);

        this.txtCodigo = (EditText) findViewById(R.id.txtCodigo);
        this.txtNombre = (EditText) findViewById(R.id.txtNombre);
        this.txtEdad = (EditText)findViewById(R.id.txtEdad);
        this.btnGuardar = (Button) findViewById(R.id.btnGuardar);
        this.btnBuscar = (Button) findViewById(R.id.btnBuscar);
        //endregion

        this.Alumnos = new ArrayList<>();
        this.btnBuscar = (Button) findViewById(R.id.btnBuscar);
        this.Listado_ALumnos = (ListView) findViewById(Listado_Alumnos);
        this.Controlador = new controllerAlumno(this);
        this.Consultar_Alumnos();
        this.adaptadorAlumno = new adapterProductos(this,R.layout.plantillaalumno, this.Alumnos);
        this.Listado_ALumnos.setAdapter(this.adaptadorAlumno);


        // Listener para el BTNGuardar
        this.btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Alumno c = new Alumno();
                Principal.this.Agregar();
                Alumnos.add(c);
                adaptadorAlumno.notifyDataSetChanged();
            }
        });

        this.btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Principal.this, Buscar.class);
                startActivity(intent);
            }
        });

    }


    private void Agregar()
    {
        if(ValidarDatos())
        {
            this.Modelo = new Alumno();
            this.Modelo.setCodigo(this.txtCodigo.getText().toString());
            this.Modelo.setNombre(this.txtNombre.getText().toString());
            this.Modelo.setEdad(Integer.parseInt(this.txtEdad.getText().toString()));

            try
            {
                //Realizamos la insercción en la base de datos
                this.Controlador.Abrir();

                //INsertamos y validamos
                if (this.Controlador.Insertar(this.Modelo))
                {
                    Toast.makeText(this, "Producto Insertado con Éxito", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Error al Insertar el Producto", Toast.LENGTH_SHORT).show();
                }

            }catch (SQLiteException ex)
            {
                Toast.makeText(this, "Error en la base de datos!", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Existen Campos sin Completar... ", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean ValidarDatos()
    {
        String pCodigo = this.txtCodigo.getText().toString();
        String pNombre = this.txtNombre.getText().toString();
        String pEdad = this.txtEdad.getText().toString();

        if(pCodigo.equals("") || pCodigo.length() == 0 || pCodigo == null)
        {
            Toast.makeText(this, "El Código no debe estar Vacío", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(pNombre.equals("") || pNombre.length() == 0 || pNombre == null)
        {
            Toast.makeText(this, "El Nombre no debe estar Vacío", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(pEdad.equals("") || pEdad.length() == 0 || pEdad == null)
        {
            return false;
        }
        else if(Integer.parseInt(pEdad) <= 0)
        {
            Toast.makeText(this, "Edad tiene que ser Número Natural", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void Limpiar()
    {
        this.txtCodigo.setText("");
        this.txtNombre.setText("");
        this.txtEdad.setText("");
    }
}
