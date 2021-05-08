package org.iesalandalus.programacion.biblioteca.mvc.vista.grafica.controladoresvistas;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.iesalandalus.programacion.biblioteca.mvc.controlador.IControlador;
import org.iesalandalus.programacion.biblioteca.mvc.modelo.dominio.Alumno;
import org.iesalandalus.programacion.biblioteca.mvc.modelo.dominio.AudioLibro;
import org.iesalandalus.programacion.biblioteca.mvc.modelo.dominio.Libro;
import org.iesalandalus.programacion.biblioteca.mvc.modelo.dominio.LibroEscrito;
import org.iesalandalus.programacion.biblioteca.mvc.modelo.dominio.Prestamo;
import org.iesalandalus.programacion.biblioteca.mvc.vista.grafica.recursos.LocalizadorRecursos;
import org.iesalandalus.programacion.biblioteca.mvc.vista.grafica.utilidades.Dialogos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControladorVentanaPrincipal {

	private IControlador controladorMVC;

	private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final String PUNTOS = "puntos";

	@FXML private MenuItem bSalir;
	@FXML private MenuItem bAcercaDe;

	@FXML private TableView<Alumno> tvAlumnos;
    @FXML private TableColumn<Alumno, String> tcANombre;
    @FXML private TableColumn<Alumno, String> tcACorreo;
    @FXML private TableColumn<Alumno, String> tcACurso;

    @FXML private TableView<Prestamo> tvPrestamoAlumno;
    @FXML private TableColumn<Prestamo, String> tcPaLibro;
    @FXML private TableColumn<Prestamo, String> tcPaFechaPrestamo;
    @FXML private TableColumn<Prestamo, String> tcPaFechaDevolucion;
    @FXML private TableColumn<Prestamo, String> tcPaPuntos;

    @FXML private TableView<Libro> tvLibros;
    @FXML private TableColumn<Libro, String> tcLTitulo;
    @FXML private TableColumn<Libro, String> tcLAutor;
    @FXML private TableColumn<Libro, String> tcLNumPaginasDuracion;

    @FXML private TableView<Prestamo> tvLibrosPrestados;
    @FXML private TableColumn<Prestamo, String> tcLpAlumno;
    @FXML private TableColumn<Prestamo, String> tcLpFechaPrestamo;
    @FXML private TableColumn<Prestamo, String> tcLpFechaDevolucion;
    @FXML private TableColumn<Prestamo, String> tcLpPuntos;

    @FXML private TableView<Prestamo> tvPrestamos;
    @FXML private TableColumn<Prestamo, String> tcPAlumno;
    @FXML private TableColumn<Prestamo, String> tcPLibro;
    @FXML private TableColumn<Prestamo, String> tcPFechaPrestamo;
    @FXML private TableColumn<Prestamo, String> tcPFechaDevolucion;
    @FXML private TableColumn<Prestamo, String> tcPPuntos;
    
    private ObservableList<Alumno> alumnos = FXCollections.observableArrayList();
	private ObservableList<Prestamo> prestamosAlumno = FXCollections.observableArrayList();
	private ObservableList<Libro> libros = FXCollections.observableArrayList();
	private ObservableList<Prestamo> prestamosLibro = FXCollections.observableArrayList();
	private ObservableList<Prestamo> prestamos = FXCollections.observableArrayList();

	private Stage insertarAlumno;
	private Stage insertarLibro;
	private Stage prestarLibro;
	private Stage devolverLibro;

	private ControladorInsertarAlumno controladorInsertarAlumno;
	private ControladorInsertarLibro controladorInsertarLibro;
	private ControladorRealizarPrestamo controladorRealizarPrestamo;
	private ControladorRealizarDevolucion controladorRealizarDevolucion;


	public void setControladorMVC(IControlador controladorMVC) {

		this.controladorMVC = controladorMVC;
	}
	
	@FXML
	private void initialize() {

		tcANombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		tcACorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
		tcACurso.setCellValueFactory(new PropertyValueFactory<>("curso"));
		tvAlumnos.setItems(alumnos);
		tvAlumnos.getSelectionModel().selectedItemProperty().addListener((ob, va, vn) -> mostrarPrestamosAlumno(vn));

		tcPaLibro.setCellValueFactory(prestamo -> new SimpleStringProperty(prestamo.getValue().getLibro().getTitulo() + ", " + prestamo.getValue().getLibro().getAutor()));
		tcPaFechaPrestamo.setCellValueFactory(prestamo -> new SimpleStringProperty(FORMATO_FECHA.format(prestamo.getValue().getFechaPrestamo())));
		tcPaFechaDevolucion.setCellValueFactory(prestamo -> new SimpleStringProperty(mostrarFechaDevolucion(prestamo.getValue())));
		tcPaPuntos.setCellValueFactory(new PropertyValueFactory<>(PUNTOS));
		tvPrestamoAlumno.setItems(prestamosAlumno);
		
	}
	
	@FXML
	private void acercaDe(ActionEvent event) throws IOException {

		VBox contenido = FXMLLoader.load(LocalizadorRecursos.class.getResource("vistas/AcercaDe.fxml"));
		Dialogos.mostrarDialogoInformacionPersonalizado("Biblioteca Yebra", contenido);
	}

	@FXML
	private void salir(ActionEvent event) {

		if (Dialogos.mostrarDialogoConfirmacion("Salir", "¿Estás seguro que desea salir de la aplicación?", null)) {

			controladorMVC.terminar();
			System.exit(0);
		}
	}
	
	@FXML
	void insertarAlumno(ActionEvent event) throws IOException {

		crearInsertarAlumno();
		insertarAlumno.showAndWait();
	}

	private void crearInsertarAlumno() throws IOException {

		if (insertarAlumno == null) {

			insertarAlumno = new Stage();

			FXMLLoader cargarInsertarAlumno = new FXMLLoader(
					LocalizadorRecursos.class.getResource("vistas/InsertarAlumno.fxml"));

			VBox raizInsertarAlumno = cargarInsertarAlumno.load();
			controladorInsertarAlumno = cargarInsertarAlumno.getController();
			controladorInsertarAlumno.setControladorMVC(controladorMVC);
			controladorInsertarAlumno.setAlumnos(alumnos);
			controladorInsertarAlumno.inicializa();

			Scene escenaInsertarAlumno = new Scene(raizInsertarAlumno);
			insertarAlumno.setTitle("Insertar Alumno");
			insertarAlumno.initModality(Modality.APPLICATION_MODAL);
			insertarAlumno.setScene(escenaInsertarAlumno);

		} else {

			controladorInsertarAlumno.inicializa();
		}
	}
	
	@FXML
	void realizarPrestamoAlumno(ActionEvent event) throws IOException {

		crearRealizarPrestamoAlumno();
		prestarLibro.showAndWait();
	}

	private void crearRealizarPrestamoAlumno() throws IOException {

		if (prestarLibro == null) {

			prestarLibro = new Stage();
			FXMLLoader cargarRealizarPrestamo = new FXMLLoader(
					LocalizadorRecursos.class.getResource("vistas/RealizarPrestamo.fxml"));

			VBox raizRealizarPrestamo = cargarRealizarPrestamo.load();
			controladorRealizarPrestamo = cargarRealizarPrestamo.getController();
			controladorRealizarPrestamo.setControladorMVC(controladorMVC);
			controladorRealizarPrestamo.setLibros(libros);
			controladorRealizarPrestamo.setAlumnos(alumnos);
			controladorRealizarPrestamo.setPrestamos(prestamos);
			controladorRealizarPrestamo.seleccionarAlumno(tvAlumnos.getSelectionModel().getSelectedItem());

			Scene escenaRealizarPrestamo = new Scene(raizRealizarPrestamo);
			prestarLibro.setTitle("Realizar Préstamo");
			prestarLibro.initModality(Modality.APPLICATION_MODAL);
			prestarLibro.setScene(escenaRealizarPrestamo);

			controladorRealizarPrestamo.inicializa();

		} else {

			controladorRealizarPrestamo.inicializa();
			controladorRealizarPrestamo.seleccionarAlumno(tvAlumnos.getSelectionModel().getSelectedItem());
		}
	}

	@FXML
	void realizarDevolucionAlumno(ActionEvent event) throws IOException {

		if (tvPrestamoAlumno.getSelectionModel().getSelectedItem() == null) {

			Dialogos.mostrarDialogoError("Realizar Devolución", "No se ha seleccionado ningún préstamo");

		} else {

			crearRealizarDevolucionAlumno();
			devolverLibro.showAndWait();
		}
	}

	private void crearRealizarDevolucionAlumno() throws IOException {

		if (devolverLibro == null) {

			devolverLibro = new Stage();
			FXMLLoader cargarRealizarDevolucion = new FXMLLoader(
					LocalizadorRecursos.class.getResource("vistas/RealizarDevolucion.fxml"));

			VBox raizRealizarDevolucion = cargarRealizarDevolucion.load();
			controladorRealizarDevolucion = cargarRealizarDevolucion.getController();
			controladorRealizarDevolucion.setControladorMVC(controladorMVC);
			controladorRealizarDevolucion.setControladorVentanaPrincipal(this);
			controladorRealizarDevolucion.setPrestamo(tvPrestamoAlumno.getSelectionModel().getSelectedItem());

			Scene escenaRealizarDevolucion = new Scene(raizRealizarDevolucion);
			devolverLibro.setTitle("Realizar Devolución");
			devolverLibro.initModality(Modality.APPLICATION_MODAL);
			devolverLibro.setScene(escenaRealizarDevolucion);

			controladorRealizarDevolucion.inicializa();

		} else {

			controladorRealizarDevolucion.setPrestamo(tvPrestamoAlumno.getSelectionModel().getSelectedItem());
			controladorRealizarDevolucion.inicializa();
		}
	}
	
	@FXML
	void eliminarAlumno(ActionEvent event) throws IOException {

		Alumno alumno = null;

		try {
			
			alumno = tvAlumnos.getSelectionModel().getSelectedItem();

			if (alumno != null && Dialogos.mostrarDialogoConfirmacion("Eliminar Alumno",
					"¿Estás seguro que desea eliminar al alumno?", null)) {

				controladorMVC.borrar(alumno);
				alumnos.remove(alumno);
				prestamosAlumno.clear();
				actualizarAlumnos();
				Dialogos.mostrarDialogoInformacion("Eliminar Alumno", "Alumno eliminado correctamente");
			}

		} catch (Exception e) {

			Dialogos.mostrarDialogoError("Eliminar Alumno", e.getMessage());
		}
	}
	
	@FXML
	void anularPrestamoAlumno(ActionEvent event) throws IOException {

		Prestamo prestamo = null;

		try {
			
			prestamo = tvPrestamoAlumno.getSelectionModel().getSelectedItem();

			if (prestamo != null && Dialogos.mostrarDialogoConfirmacion("Anular Préstamo",
					"¿Estás seguro que desea anular el préstamo?", null)) {

				controladorMVC.borrar(prestamo);
				prestamos.remove(prestamo);
				actualizarPrestamos();
				actualizarAlumnos();
				actualizarLibros();
				Dialogos.mostrarDialogoInformacion("Anular Préstamo", "Préstamo anulado correctamente");
			}

		} catch (Exception e) {

			Dialogos.mostrarDialogoError("Anular Prestamo", e.getMessage());
		}
	}
	
	public void actualizarAlumnos() {

		prestamosAlumno.clear();
		tvLibros.getSelectionModel().clearSelection();
		alumnos.setAll(controladorMVC.getAlumnos());
	}

	public void actualizarLibros() {

		prestamosLibro.clear();
		tvAlumnos.getSelectionModel().clearSelection();
		libros.setAll(controladorMVC.getLibros());
	}

	public void actualizarPrestamos() {

		prestamos.clear();
		tvPrestamos.getSelectionModel().clearSelection();
		prestamos.setAll(controladorMVC.getPrestamos());
	}
	
	public void mostrarPrestamosAlumno(Alumno alumno) {

		try {

			if (alumno != null) {

				prestamosAlumno.setAll(controladorMVC.getPrestamos(alumno));
				tvPrestamoAlumno.setItems(prestamosAlumno);
			}

		} catch (IllegalArgumentException e) {

			prestamosAlumno.setAll(FXCollections.observableArrayList());
		}

		actualizarPrestamos();
	}

	private String mostrarFechaDevolucion(Prestamo prestamo) {

		if (prestamo.getFechaDevolucion() == null) {

			return "";

		} else {

			return FORMATO_FECHA.format(prestamo.getFechaDevolucion());
		}
	}
}
