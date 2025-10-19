package com.alvespatrick.usuario.business;

import com.alvespatrick.usuario.business.converter.UsuarioConverter;
import com.alvespatrick.usuario.business.dto.UsuarioDTO;
import com.alvespatrick.usuario.infrastructure.entity.Usuario;
import com.alvespatrick.usuario.infrastructure.exceptions.ConflictException;
import com.alvespatrick.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.alvespatrick.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder bCryptPasswordEncoder;

    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO) {

        emailExistente(usuarioDTO.getEmail());
        usuarioDTO.setSenha(bCryptPasswordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(
                usuarioRepository.save(usuario)
        );

    }

    public void emailExistente(String email) {
        try {
            boolean existe = verificarEmailExistente(email);
            if (existe) {
                throw new ConflictException("Email ja cadastrado" + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("Email ja cadastrado" + e.getCause());
        }
    }

    public boolean verificarEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);


    }
    public Usuario buscaUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(

                () -> new ResourceNotFoundException("Email não encontrado" + email ));
    }
    public void  deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }
}

