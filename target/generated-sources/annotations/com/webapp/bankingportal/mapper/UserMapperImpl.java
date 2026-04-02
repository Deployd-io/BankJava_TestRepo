package com.webapp.bankingportal.mapper;

import com.webapp.bankingportal.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-02T12:35:43+0000",
    comments = "version: 1.6.0.Beta2, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public void updateUser(User source, User target) {
        if ( source == null ) {
            return;
        }

        if ( source.getId() != null ) {
            target.setId( source.getId() );
        }
        if ( source.getName() != null ) {
            target.setName( source.getName() );
        }
        if ( source.getPassword() != null ) {
            target.setPassword( source.getPassword() );
        }
        if ( source.getEmail() != null ) {
            target.setEmail( source.getEmail() );
        }
        if ( source.getCountryCode() != null ) {
            target.setCountryCode( source.getCountryCode() );
        }
        if ( source.getPhoneNumber() != null ) {
            target.setPhoneNumber( source.getPhoneNumber() );
        }
        if ( source.getAddress() != null ) {
            target.setAddress( source.getAddress() );
        }
        if ( source.getAccount() != null ) {
            target.setAccount( source.getAccount() );
        }
    }
}
