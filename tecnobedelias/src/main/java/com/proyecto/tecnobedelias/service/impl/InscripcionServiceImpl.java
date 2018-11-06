package com.proyecto.tecnobedelias.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.tecnobedelias.persistence.model.Asignatura;
import com.proyecto.tecnobedelias.persistence.model.Carrera;
import com.proyecto.tecnobedelias.persistence.model.Curso;
import com.proyecto.tecnobedelias.persistence.model.Curso_Estudiante;
import com.proyecto.tecnobedelias.persistence.model.Asignatura_Carrera;
import com.proyecto.tecnobedelias.persistence.model.Examen;
import com.proyecto.tecnobedelias.persistence.model.Estudiante_Examen;
import com.proyecto.tecnobedelias.persistence.model.Usuario;
import com.proyecto.tecnobedelias.persistence.repository.Asignatura_CarreraRepository;
import com.proyecto.tecnobedelias.persistence.repository.CarreraRepository;
import com.proyecto.tecnobedelias.persistence.repository.CursoRepository;
import com.proyecto.tecnobedelias.persistence.repository.Curso_EstudianteRepository;
import com.proyecto.tecnobedelias.persistence.repository.Estudiante_ExamenRepository;
import com.proyecto.tecnobedelias.persistence.repository.ExamenRepository;
import com.proyecto.tecnobedelias.persistence.repository.UsuarioRepository;
import com.proyecto.tecnobedelias.service.EmailService;
import com.proyecto.tecnobedelias.service.InscripcionService;

@Service
public class InscripcionServiceImpl implements InscripcionService {

	@Autowired
	CarreraRepository carreraRepository;
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	CursoRepository cursoRepository;
	
	@Autowired
	ExamenRepository examenRepository;

	@Autowired
	Curso_EstudianteRepository cursoEstudianteRepository;

	@Autowired
	Estudiante_ExamenRepository estudianteExamenRepository;
	
	@Autowired
	Asignatura_CarreraRepository asignaturaCarreraRepository;
	
	@Autowired
	EmailService emailService;
	
	@Override
	public boolean inscripcionCarrera(Usuario usuario, Carrera carrera) {
		if (usuario.getCarreras().contains(carrera)) {
			return false;
		} else {
			usuario.getCarreras().add(carrera);
			//usuarioRepository.save(usuario);
			carrera.getEstudiantes().add(usuario);
			carreraRepository.save(carrera);						
			return true;
		}
	}
	
	/*private boolean isElectivaAsignaturaCarrera(Asignatura asignatura, Carrera carrera) {
		Optional<Asignatura_Carrera> asign_carreraExistente = asignaturaCarreraRepository.findByAsignaturaAndCarrera(asignatura, carrera);
		if (asign_carreraExistente.isPresent()) 
			if (asign_carreraExistente.get().isElectiva())
				return true;
			else return false;
		else return false;			
	}*/
	
	private boolean isAsignaturaEnCarreraEstudiante(Asignatura asignatura, Usuario estudiante) {
		boolean asignaturaEnCarrera = false;			
		Iterator<Carrera> itCarreras = estudiante.getCarreras().iterator();
		while (itCarreras.hasNext() && !asignaturaEnCarrera) {
			Iterator<Asignatura_Carrera> itAsignCarreras = itCarreras.next().getAsignaturaCarrera().iterator();
			while (itAsignCarreras.hasNext() && !asignaturaEnCarrera) {
				if (itAsignCarreras.next().getAsignatura().equals(asignatura))
					asignaturaEnCarrera = true;
			}
		}
		return asignaturaEnCarrera;
	}	
	
	
	public Curso obtenerUltimoCursoAsignaturaEstudiante(Asignatura asignatura, Usuario usuario) {	
		Curso ultimoCursoAsignaturaEstudiante = null;
		try {		
			if (!asignatura.getCursos().isEmpty()) {
				SimpleDateFormat formateadorfecha = new SimpleDateFormat("yyyy-MM-dd"); 
				String fechaFinCursoString;
				String fechaActualString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());			
				Date fechaActualDate = formateadorfecha.parse(fechaActualString);
				long fechaActual = fechaActualDate.getTime();
				Date fechaFinCursoDate;
				long fechaFinCurso;
				long diferencia = Long.MAX_VALUE;
				for (Curso curso : asignatura.getCursos()) {
					for (Curso_Estudiante cursoEstudiante : curso.getCursoEstudiante()) {
						if (cursoEstudiante.getEstudiante().equals(usuario)) {
							fechaFinCursoString = new SimpleDateFormat("yyyy-MM-dd").format(curso.getFechaFin());
							fechaFinCursoDate = formateadorfecha.parse(fechaFinCursoString);
							fechaFinCurso = fechaFinCursoDate.getTime();
							if (fechaActual - fechaFinCurso < diferencia) {
								diferencia = fechaActual - fechaFinCurso;
								ultimoCursoAsignaturaEstudiante = curso;
							}
						}
					}				
				}
			}		
		} catch (Exception e) {
			System.out.println("Ha ocurrido una excepcion.");
		}
		return ultimoCursoAsignaturaEstudiante;
	}
	
	
	public Examen obtenerUltimoExamenAsignaturaEstudiante(Asignatura asignatura, Usuario usuario) {	
		Examen ultimoExamenAsignaturaEstudiante = null;
		try {		
			if (!asignatura.getExamenes().isEmpty()) {
				SimpleDateFormat formateadorfecha = new SimpleDateFormat("yyyy-MM-dd"); 
				String fechaExamenString;
				String fechaActualString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());			
				Date fechaActualDate = formateadorfecha.parse(fechaActualString);
				long fechaActual = fechaActualDate.getTime();
				Date fechaExamenDate;
				long fechaExamen;
				long diferencia = Long.MAX_VALUE;
				for (Examen examen : asignatura.getExamenes()) {
					for (Estudiante_Examen estudianteExamen : examen.getEstudianteExamen()) {
						if (estudianteExamen.getEstudiante().equals(usuario)) {
							fechaExamenString = new SimpleDateFormat("yyyy-MM-dd").format(examen.getFecha());
							fechaExamenDate = formateadorfecha.parse(fechaExamenString);
							fechaExamen = fechaExamenDate.getTime();
							if (fechaActual - fechaExamen < diferencia) {
								diferencia = fechaActual - fechaExamen;
								ultimoExamenAsignaturaEstudiante = examen;
							}
						}
					}				
				}
			}		
		} catch (Exception e) {
			System.out.println("Ha ocurrido una excepcion.");
		}
		return ultimoExamenAsignaturaEstudiante;
	}
	
	@Override
	public boolean inscripcionCurso(Usuario usuario, Curso curso) {
		Optional<Curso_Estudiante> cursoEstudianteExistente = cursoEstudianteRepository.findByCursoAndEstudiante(curso,	usuario);
		boolean matriculado = false;
		boolean puedeMatricularse = true; 
		// si ya está matriculado o salvo la asignatura correspondiente al curso no puede matricularse 
		if (cursoEstudianteExistente.isPresent()) {
			//if (cursoEstudianteExistente.get().getEstado().equals("MATRICULADO") || cursoEstudianteExistente.get().getEstado().equals("SALVADO")) {	
				puedeMatricularse = false;
				System.out.println("El estudiante ya esta matriculado");
			//}
		}
		if (isAprobadaAsignaturaEstudiante(curso.getAsignatura(), usuario)) {
			System.out.println("El estudiante ya tiene aprobada la asignatura");
			puedeMatricularse = false;	
		}
		// si no recorro las carreras en las que el usuario esta inscripto y me fijo si la asignatura del curso pertenece a una de estas carreras
		if (puedeMatricularse) {
			System.out.println("El estudiante no esta matriculado");
			/*for (Carrera carrera : usuario.getCarreras()) {
				for (Asignatura_Carrera asign_carrera : carrera.getAsignaturaCarrera()) {
					if (asign_carrera.getAsignatura().equals(curso.getAsignatura()))
						encontrada = true;
				}				
			}*/			
			if (isAsignaturaEnCarreraEstudiante(curso.getAsignatura(), usuario)) {
				System.out.println("La asignatura pertenece a una carrera en la que esta inscripto el usuario");
				boolean cumplePreviaturas = true;
				// para cada carrera en la que el estudiante está inscripto
				if (!usuario.getCarreras().isEmpty()) {					
					for (Carrera carrera : usuario.getCarreras()) {
						System.out.println("carrera: "+carrera.getNombre());
						// para cada asignatura en una carrera en la que el estudiante está inscripto
						//if (!carrera.getAsignaturaCarrera().isEmpty()) {
							for (Asignatura_Carrera asign_carrera : carrera.getAsignaturaCarrera()) {
								// si la asignatura en la carrera es la del curso al que el usuario intenta matricularse
								if (asign_carrera.getAsignatura().equals(curso.getAsignatura())) {
									System.out.println("asignatura: "+asign_carrera.getAsignatura().getNombre());
									// para cada asignatura en la carrera previa a la del curso al que el usuario intenta matricularse
									for (Asignatura_Carrera previa_asign_carrera : asign_carrera.getPrevias()) {
										System.out.println("asignatura previa: "+previa_asign_carrera.getAsignatura().getNombre());
										// si la asignatura previa es taller solo se verifican el ultimo curso
										if (previa_asign_carrera.getAsignatura().isTaller()) {
											// para el ultimo curso de dicha asignatura previa en la carrera para el usuario dado 
											System.out.println("La asignatura previa es taller");
											if (!previa_asign_carrera.getAsignatura().getCursos().isEmpty()) {
												Curso ultimo_curso_previa = obtenerUltimoCursoAsignaturaEstudiante(previa_asign_carrera.getAsignatura(), usuario);
												if (ultimo_curso_previa != null) {
													Curso_Estudiante ultimo_curso_estudiante = cursoEstudianteRepository.findByCursoAndEstudiante(ultimo_curso_previa, usuario).get();
													if (!ultimo_curso_estudiante.getEstado().equals("MATRICULADO")) {
														System.out.println("el estado no es matriculado");
														// si se verifica que no está salvado el ultimo curso de la asignatura previa
														if (!ultimo_curso_estudiante.getEstado().equals("SALVADO")) {
															// no cumple las previaturas
															cumplePreviaturas = false;	
														}
													}
													else cumplePreviaturas = false;
												}
												else cumplePreviaturas = false;
											}
											else cumplePreviaturas = false;
										}
										// si la asignatura previa no es electiva en la carrera
										else {
											System.out.println("La asignatura previa no es taller");
											// para cada curso de la asignatura previa en la carrera 
											if (!previa_asign_carrera.getAsignatura().getCursos().isEmpty()) {
												Curso ultimo_curso_previa = obtenerUltimoCursoAsignaturaEstudiante(previa_asign_carrera.getAsignatura(), usuario);												
												if (ultimo_curso_previa != null) {
													Curso_Estudiante ultimo_curso_estudiante = cursoEstudianteRepository.findByCursoAndEstudiante(ultimo_curso_previa, usuario).get();
													if (!ultimo_curso_estudiante.getEstado().equals("MATRICULADO")) {
														System.out.println("el estado no es matriculado");
														if (!ultimo_curso_estudiante.getEstado().equals("SALVADO")) {
															System.out.println("no tiene salvado el curso de la asignatura previa");
															// si se verifica que tiene aprobado el curso de la asignatura previa (ganado el derecho a examen)
															if (ultimo_curso_estudiante.getEstado().equals("EXAMEN")) {
																System.out.println("tiene aprobado el curso de la asignatura previa");
																// para cada examen del estudiante que pretende inscribirse al curso
																if (!usuario.getEstudianteExamen().isEmpty()) {
																	Examen ultimo_examen_previa = obtenerUltimoExamenAsignaturaEstudiante(previa_asign_carrera.getAsignatura(), usuario);
																	if (ultimo_examen_previa != null) {
																		Estudiante_Examen ultimo_estudiante_examen = estudianteExamenRepository.findByExamenAndEstudiante(ultimo_examen_previa, usuario).get();
																		if (!ultimo_estudiante_examen.getEstado().equals("ANOTADO")) {
																			if (!ultimo_estudiante_examen.getEstado().equals("APROBADO")) {
																				System.out.println("no tiene aprobado el examen de la asignatura previa");
																				// no cumple la previatura
																				cumplePreviaturas = false;
																			}																	
																		}
																		else cumplePreviaturas = false;
																	}
																	else cumplePreviaturas = false;
																}
																else cumplePreviaturas = false;
															}
															else cumplePreviaturas = false;
														}														
													}
													else cumplePreviaturas = false;
												}
												else cumplePreviaturas = false;
											}
										}							
									}
								}
						//}
						//else cumplePreviaturas = false;
							}
					}	
				}
				else cumplePreviaturas = false;
				if (cumplePreviaturas) {				
					// anoto al estudiante en el curso
					Curso_Estudiante cursoEstudiante = new Curso_Estudiante();
					cursoEstudiante.setCurso(curso);
					cursoEstudiante.setEstudiante(usuario);
					cursoEstudiante.setNota(-1);
					cursoEstudiante.setEstado("MATRICULADO");
					cursoEstudiante.setNombre(usuario.getNombre());
					cursoEstudiante.setApellido(usuario.getApellido());
					cursoEstudiante.setCedula(usuario.getCedula());
					usuario.getCursoEstudiante().add(cursoEstudiante);
					curso.getCursoEstudiante().add(cursoEstudiante);					
					cursoEstudianteRepository.save(cursoEstudiante);
					matriculado = true;				 				
				}
			}
		}		
		return matriculado;
	}	
	
	
	public boolean isAprobadaAsignaturaEstudiante(Asignatura asignatura, Usuario usuario) {
		boolean aprobada = false;
		Iterator<Curso_Estudiante> itCursoEst = usuario.getCursoEstudiante().iterator();
		// reviso todos los cursos del el estudiante hasta que no haya mas o lo tenga salvado 
		while (itCursoEst.hasNext() && !aprobada) {
			// si la asignatura del curso coincide con la pasada como parametro
			Curso_Estudiante curso_est = itCursoEst.next();
			if (curso_est.getCurso().getAsignatura().equals(asignatura)) {
				if (curso_est.getEstado().equals("SALVADO")) {
					aprobada = true;
				}
			}
		}
		Iterator<Estudiante_Examen> itEstExamen = usuario.getEstudianteExamen().iterator();
		// reviso todos los examenes del estudiante hasta que no haya mas o lo tenga aprobado 
		while (itEstExamen.hasNext() && !aprobada) {
			// si la asignatura del curso coincide con la pasada como parametro
			Estudiante_Examen est_examen = itEstExamen.next();
			if (est_examen.getExamen().getAsignatura().equals(asignatura)) {
				if (est_examen.getEstado().equals("APROBADO")) {
					aprobada = true;
				}
			}
		}
		return aprobada;
	}

	
	@Override
	public boolean inscripcionExamen(Usuario usuario, Examen examen) {		
		Optional<Estudiante_Examen> estudianteExamenExistente = estudianteExamenRepository.findByExamenAndEstudiante(examen, usuario);
		boolean anotado = false;
		boolean puedeAnotarse = true;
		// si ya esta inscripto al examen no puede volver a anotarse
		if (!examen.getAsignatura().isTaller()) {
			if (estudianteExamenExistente.isPresent()) {// si ya está matriculado o salvo la asignatura correspondiente al curso no puede matricularse 
				//if (estudianteExamenExistente.get().getEstado().equals("ANOTADO") || estudianteExamenExistente.get().getEstado().equals("APROBADO")) {
					System.out.println("El estudiante ya esta anotado");
					puedeAnotarse = false;	
				//}
			}
			if (isAprobadaAsignaturaEstudiante(examen.getAsignatura(), usuario)) {
				System.out.println("El estudiante ya tiene aprobada la asignatura");
				puedeAnotarse = false;	
			}
			//Curso ultimocurso = obtenerUltimoCursoAsignaturaEstudiante(examen.getAsignatura(), usuario);
			if (puedeAnotarse) {			
				// si la asignatura del examen al cual se quiere anotar pertenece a una carrera en que esté matriculado	
				if (isAsignaturaEnCarreraEstudiante(examen.getAsignatura(), usuario)) {
					try {
						System.out.println("La asignatura del examen pertenece a una carrera del estudiante");
						Iterator<Curso_Estudiante> itCursoEst = usuario.getCursoEstudiante().iterator();
						// reviso todos los cursos donde esta matriculado el estudiante hasta que no haya mas o quede anotado al examen 
						Date fechaActualDate = new Date();
						SimpleDateFormat formateadorfecha = new SimpleDateFormat("yyyy-MM-dd");
						String fechaActualString = new SimpleDateFormat("yyyy-MM-dd").format(fechaActualDate);
						Date fechaActual = formateadorfecha.parse(fechaActualString);
						Calendar calendar;
						String fechaFinMas2AniosString;
						Date fechaFinMas2Anios;
						String fechaFinString;
						Date fechaFin;
						String fechaExamenString;
						Date fechaExamen;
						int examenes_rendidos;
						while (itCursoEst.hasNext() && !anotado) {
							// si la asignatura del curso coincide con la del examen
							Curso_Estudiante curso_est = itCursoEst.next();
							if (curso_est.getCurso().getAsignatura().equals(examen.getAsignatura())) {
								System.out.println("estado estudiante en curso "+curso_est.getEstado());
								// si el estudiante tiene ganado el curso para el que se encuentra matriculado en la asignatura del examen
								if (curso_est.getEstado().equals("EXAMEN")) {
									calendar = Calendar.getInstance();
									calendar.setTime(curso_est.getCurso().getFechaFin());
									calendar.add(Calendar.YEAR, 2);		
									fechaExamenString = new SimpleDateFormat("yyyy-MM-dd").format(examen.getFecha());
									fechaExamen = formateadorfecha.parse(fechaExamenString);
									fechaFinMas2AniosString = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
									fechaFinMas2Anios = formateadorfecha.parse(fechaFinMas2AniosString);
									fechaFinString = new SimpleDateFormat("yyyy-MM-dd").format(curso_est.getCurso().getFechaFin());
									fechaFin = formateadorfecha.parse(fechaFinString);
									/*System.out.println("El estudiante tiene ganado el curso");
									System.out.println("fecha fin "+fechaFin);
									System.out.println("fecha fin + 2 anios "+fechaFinMas2Anios);
									System.out.println("fecha examen "+fechaExamen);*/
									if (fechaActual.before(fechaFinMas2Anios) && fechaExamen.before(fechaFinMas2Anios)) {
										System.out.println("fecha actual < fecha fin curso + 2 anios");
										examenes_rendidos = 0;										
										Iterator<Estudiante_Examen> itEstExamen = usuario.getEstudianteExamen().iterator();
										while (itEstExamen.hasNext()) {
											Estudiante_Examen est_examen = itEstExamen.next();
											if (est_examen.getExamen().getAsignatura().equals(examen.getAsignatura())) {												
												System.out.println("asignatura "+examen.getAsignatura().getNombre());					
												if (fechaExamen.after(fechaFin)) {
													examenes_rendidos = examenes_rendidos + 1;													
												}								
											}
										}
										if (examenes_rendidos < 3) {
											System.out.println("examenes rendidios "+examenes_rendidos);
											Estudiante_Examen estudianteExamen = new Estudiante_Examen();
											estudianteExamen.setEstudiante(usuario);
											usuario.getEstudianteExamen().add(estudianteExamen);
											estudianteExamen.setExamen(examen);
											examen.getEstudianteExamen().add(estudianteExamen);
											estudianteExamen.setNombre(usuario.getNombre());
											estudianteExamen.setApellido(usuario.getApellido());
											estudianteExamen.setCedula(usuario.getCedula());
											estudianteExamen.setEstado("ANOTADO");
											estudianteExamen.setNota(-1);
											estudianteExamenRepository.save(estudianteExamen);
											anotado = true;	
										}
									}
								}
							}
						}
					} 
					catch (Exception e) {
						System.out.println("Excepcion en las fechas");
					} 
				}
			}
		}		
		return anotado;			
	}

	

	@Override
	public boolean desistirCurso(Usuario usuario, Curso curso) {
		Optional<Curso_Estudiante> cursoEstudianteExistente = cursoEstudianteRepository.findByCursoAndEstudiante(curso,	usuario);
		if (cursoEstudianteExistente.isPresent()) {	
			//System.out.println("existe el estudiante en el curso");
			if (cursoEstudianteExistente.get().getEstado().equals("MATRICULADO")) {
				usuario.getCursoEstudiante().remove(cursoEstudianteExistente.get());
				curso.getCursoEstudiante().remove(cursoEstudianteExistente.get());
				cursoEstudianteRepository.delete(cursoEstudianteExistente.get());
				return true;
			}
			else return false;
		} 
		else return false;		
	}

	@Override
	public boolean desistirExamen(Usuario usuario, Examen examen) {		
		Optional<Estudiante_Examen> estudianteExamenExistente = estudianteExamenRepository.findByExamenAndEstudiante(examen, usuario);
		if (estudianteExamenExistente.isPresent()) {
			//System.out.println("existe el estudiante en el examen");
			if (estudianteExamenExistente.get().getEstado().equals("ANOTADO")) {
				usuario.getEstudianteExamen().remove(estudianteExamenExistente.get());
				examen.getEstudianteExamen().remove(estudianteExamenExistente.get());
				estudianteExamenRepository.delete(estudianteExamenExistente.get());
				return true;
			}
			else return false;
		}
		else return false;
	}

	/*@Override
	public boolean desistirCarrera(Usuario usuario, Carrera carrera) {
		if (usuario.getCarreras().contains(carrera)) {
			usuario.getCarreras().remove(carrera);
			carrera.getEstudiantes().remove(usuario);
			carreraRepository.save(carrera);
			return true;
		}
		return false;
	}*/

	@Override
	public List<Curso> consultaCursos(Usuario usuario) {
		List<Curso> listaCursos = new ArrayList<>();
		for(Curso_Estudiante cursoEstudiante : usuario.getCursoEstudiante()) {
			listaCursos.add(cursoEstudiante.getCurso());
		}
		return listaCursos;
	}	
	
	@Override
	public List<Examen> consultaExamenes(Usuario usuario) {
		List<Examen> listaExamenes = new ArrayList<>();
		for(Estudiante_Examen examenEstudiante : usuario.getEstudianteExamen()) {
			listaExamenes.add(examenEstudiante.getExamen());
		}
		return listaExamenes;
	}
	
	@Override
	public Usuario obtenerEstudianteCursoEstudiante(long id_curso_est) {
		return cursoEstudianteRepository.findById(id_curso_est).get().getEstudiante();
	}
	
	@Override
	public Usuario obtenerEstudianteEstudianteExamen(long id_est_examen) {
		return estudianteExamenRepository.findById(id_est_examen).get().getEstudiante();
	}

	@Override
	public boolean ingresarCalificacionExamen(Usuario usuario, Examen examen, int nota) {
		Optional<Estudiante_Examen> estudianteExamenExistente = estudianteExamenRepository.findByExamenAndEstudiante(examen, usuario);
		if (!usuario.getCarreras().isEmpty()) {
			boolean asignaturaEncontrada = false;			
			Carrera carrera = null; 
			for (Carrera carreraEstudiante : usuario.getCarreras()) {	
				for (Asignatura_Carrera asignaturaCarreraEstudiante: carreraEstudiante.getAsignaturaCarrera()) {
					if (asignaturaCarreraEstudiante.getAsignatura().equals(examen.getAsignatura())) {
						asignaturaEncontrada = true;
						carrera = asignaturaCarreraEstudiante.getCarrera();
					}
				}
			}
			if (asignaturaEncontrada) {
				Optional<Asignatura_Carrera> asignaturaCarreraEstudiante = asignaturaCarreraRepository.findByAsignaturaAndCarrera(examen.getAsignatura(), carrera);
				if (estudianteExamenExistente.isPresent()) {
					if (estudianteExamenExistente.get().getEstado().equals("ANOTADO")) {
						if (nota >= 0 && nota <= asignaturaCarreraEstudiante.get().getNotaMaxima()) {
							estudianteExamenExistente.get().setNota(nota);						
							if (nota >= asignaturaCarreraEstudiante.get().getNotaSalvaExamen()) {
								estudianteExamenExistente.get().setEstado("APROBADO");
							}
							else estudianteExamenExistente.get().setEstado("REPROBADO");
							estudianteExamenRepository.save(estudianteExamenExistente.get());
							emailService.sendEmailCalifiacion("examen", usuario.getEmail(), examen.getAsignatura().getNombre());
							return true;
						}
						else return false;						
					}
					else return false;
				}
				else return false;
			}
			else return false;
		}
		else return false;
	}
	
	
	@Override
	public boolean ingresarCalificacionCurso(Usuario usuario, Curso curso, int nota) {
		Optional<Curso_Estudiante> cursoEstudianteExistente = cursoEstudianteRepository.findByCursoAndEstudiante(curso, usuario);
		if (cursoEstudianteExistente.isPresent()) {
			System.out.println("existe el estudiante en el curso");
			if (!usuario.getCarreras().isEmpty()) {
				boolean asignaturaEncontrada = false;			
				Carrera carrera = null; 
				for (Carrera carreraEstudiante : usuario.getCarreras()) {	
					for (Asignatura_Carrera asignaturaCarreraEstudiante: carreraEstudiante.getAsignaturaCarrera()) {
						if (asignaturaCarreraEstudiante.getAsignatura().equals(curso.getAsignatura())) {
							asignaturaEncontrada = true;
							carrera = asignaturaCarreraEstudiante.getCarrera();
						}
					}
				}
				if (asignaturaEncontrada) {
					System.out.println("asignatura: " + curso.getAsignatura().getNombre());
					System.out.println("carrera: " + carrera.getNombre());
					Optional<Asignatura_Carrera> asignaturaCarreraEstudiante = asignaturaCarreraRepository.findByAsignaturaAndCarrera(curso.getAsignatura(), carrera);
					if (cursoEstudianteExistente.get().getEstado().equals("MATRICULADO")) {
						if (nota >= 0 && nota <= asignaturaCarreraEstudiante.get().getNotaMaxima()) {
							cursoEstudianteExistente.get().setNota(nota);
							if (nota >= asignaturaCarreraEstudiante.get().getNotaMinimaExamen()) {
								if (nota >= asignaturaCarreraEstudiante.get().getNotaMinimaExonera()) {
									cursoEstudianteExistente.get().setEstado("SALVADO");
								}
								else cursoEstudianteExistente.get().setEstado("EXAMEN");
							}
							else cursoEstudianteExistente.get().setEstado("RECURSA");
							cursoEstudianteRepository.save(cursoEstudianteExistente.get());
							emailService.sendEmailCalifiacion("curso", usuario.getEmail(), curso.getAsignatura().getNombre());
							return true;							
						}
						else return false;						
					}
					else return false;
				}
				else return false;
			}
			else return false;
		}
		else return false;
	}
	
	
}
