package com.proyecto.SafeRoute.repository;

import com.proyecto.SafeRoute.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 * Proporciona métodos de acceso a datos de usuarios
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario
     * @param userName Nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByUserName(String userName);

    /**
     * Busca un usuario por su email
     * @param email Correo electrónico
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca usuarios por rol
     * @param rol Rol del usuario (ADMIN, PADRE, CONDUCTOR)
     * @return Lista de usuarios con ese rol
     */
    List<Usuario> findByRol(String rol);

    /**
     * Busca usuarios por rol y estado
     * @param rol Rol del usuario
     * @param activo Estado del usuario
     * @return Lista de usuarios con ese rol y estado
     */
    List<Usuario> findByRolAndActivo(String rol, Boolean activo);

    /**
     * Busca usuarios activos
     * @param activo Estado del usuario
     * @return Lista de usuarios activos/inactivos
     */
    List<Usuario> findByActivo(Boolean activo);

    /**
     * Busca usuarios por nombre (búsqueda parcial, case insensitive)
     * @param nombre Nombre del usuario
     * @return Lista de usuarios que coincidan
     */
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Verifica si existe un usuario con un username específico
     * @param userName Nombre de usuario
     * @return true si existe, false si no
     */
    boolean existsByUserName(String userName);

    /**
     * Verifica si existe un usuario con un email específico
     * @param email Correo electrónico
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Cuenta usuarios por rol
     * @param rol Rol del usuario
     * @return Cantidad de usuarios con ese rol
     */
    long countByRol(String rol);

    /**
     * Cuenta usuarios por rol y estado
     * @param rol Rol del usuario
     * @param activo Estado del usuario
     * @return Cantidad de usuarios con ese rol y estado
     */
    long countByRolAndActivo(String rol, Boolean activo);
}