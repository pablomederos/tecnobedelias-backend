package com.proyecto.tecnobedelias.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.tecnobedelias.persistence.model.Asignatura;
import com.proyecto.tecnobedelias.persistence.model.Asignatura_Carrera;
import com.proyecto.tecnobedelias.persistence.repository.AsignaturaRepository;
import com.proyecto.tecnobedelias.service.AsignaturaService;

@Service
public class AsignaturaServiceImpl implements AsignaturaService {
	
	@Autowired
	AsignaturaRepository asignaturaRepository;	
	
	@Override
	public void altaAsignatura(Asignatura asignatura) {
		asignaturaRepository.save(asignatura);
	}
	
	@Override
	public List<Asignatura> listarAsignaturas() {
		return asignaturaRepository.findAll();
	}
	
	@Override
	public boolean existeAsignatura(long asignaturaId) {
		Optional<Asignatura> asignaturaExistente = asignaturaRepository.findById(asignaturaId);
		if (asignaturaExistente.isPresent()) 
			return true;
		else return false;
	}
	
	@Override
	public boolean existeAsignaturaNombre(String nombre) {
		Optional<Asignatura> asignaturaExistente = asignaturaRepository.findByNombre(nombre);
		if (asignaturaExistente.isPresent()) 
			return true;
		else return false;
	}
	
	@Override
	public Optional<Asignatura> obtenerAsignatura(long asignaturaId) {
		return asignaturaRepository.findById(asignaturaId);
	}
	
	@Override
	public Optional<Asignatura> obtenerAsignaturaNombre(String nombre) {
		return asignaturaRepository.findByNombre(nombre);
	}
	
	@Override
	public void bajaAsignatura(Asignatura asignatura) {
		asignaturaRepository.delete(asignatura);
	}
	
	@Override
	public void modificacionAsignatura(Asignatura asignatura) {
		asignaturaRepository.save(asignatura);
	}
	
	@Override
	public Asignatura_Carrera obtenerAsignaturaCarrera(long asignaturaId) {
		Optional<Asignatura> asignaturaOpt = asignaturaRepository.findById(asignaturaId);
		if(asignaturaOpt.isPresent()) {
			if (!asignaturaOpt.get().getAsignaturaCarrera().isEmpty()) {
				return asignaturaOpt.get().getAsignaturaCarrera().get(0);				
			}else return null;
		}else return null;
	}


}
