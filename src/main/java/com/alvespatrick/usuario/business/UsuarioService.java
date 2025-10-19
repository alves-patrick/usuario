package com.alvespatrick.usuario.business;

import com.alvespatrick.usuario.business.converter.UsuarioConverter;
import com.alvespatrick.usuario.business.dto.UsuarioDTO;
import com.alvespatrick.usuario.infrastructure.entity.Usuario;
import com.alvespatrick.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvarUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(usuario);
    }


}
