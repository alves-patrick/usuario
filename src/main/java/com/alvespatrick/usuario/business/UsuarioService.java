package com.alvespatrick.usuario.business;

import com.alvespatrick.usuario.business.converter.UsuarioConverter;
import com.alvespatrick.usuario.business.dto.EnderecoDTO;
import com.alvespatrick.usuario.business.dto.TelefoneDTO;
import com.alvespatrick.usuario.business.dto.UsuarioDTO;
import com.alvespatrick.usuario.infrastructure.entity.Endereco;
import com.alvespatrick.usuario.infrastructure.entity.Telefone;
import com.alvespatrick.usuario.infrastructure.entity.Usuario;
import com.alvespatrick.usuario.infrastructure.exceptions.ConflictException;
import com.alvespatrick.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.alvespatrick.usuario.infrastructure.repository.EnderecoRepository;
import com.alvespatrick.usuario.infrastructure.repository.TelefoneRepository;
import com.alvespatrick.usuario.infrastructure.repository.UsuarioRepository;
import com.alvespatrick.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

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
    public UsuarioDTO buscaUsuarioPorEmail(String email) {
        try {
            return usuarioConverter.paraUsuarioDTO(usuarioRepository.findByEmail(email).orElseThrow(

                    () -> new ResourceNotFoundException("Email não encontrado " + email)));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Email nao encontrado " + email);
        }
    }
    public void  deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto) {

        String email = jwtUtil.extrairEmailToken(token.substring(7));

        dto.setSenha(dto.getSenha() != null ? bCryptPasswordEncoder.encode(dto.getSenha()) : null);

        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
            new ResourceNotFoundException("Email nao localizado"));

        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);


        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

        public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO) {

            Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(() ->
                    new ResourceNotFoundException("Id nao Encontrado " + idEndereco));

            Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO,entity);

            return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
        }

        public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO dto) {

            Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                    new ResourceNotFoundException("Id nao encontrado " + idTelefone));

            Telefone telefone = usuarioConverter.updateTelefone(dto, entity);
            return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
        }
        public EnderecoDTO cadastroEndereco(String token, EnderecoDTO dto) {
                String email = jwtUtil.extrairEmailToken(token.substring(7));
                Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                        new ResourceNotFoundException("Email nao localizado " + email));

                Endereco endereco = usuarioConverter.paraEnderecoEntity(dto, usuario.getId());
                Endereco enderecoEntity = enderecoRepository.save(endereco);
                return usuarioConverter.paraEnderecoDTO(enderecoEntity);
        }
    public TelefoneDTO cadastraTelefone(String token, TelefoneDTO dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email nao localizado " + email));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(dto, usuario.getId());
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }





}


