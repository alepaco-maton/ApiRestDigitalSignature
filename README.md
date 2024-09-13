# API para Firmar Docuemntos Digitales

# Descripción:

Esta API RESTful permite la gestion de usuarios y que cada usuario pueda firmar sus documentos con certificado digital.

# Funcionalidades:

Usuario
    Crear: Permite crear un usuario, adicionalmente a esto se crea un certificado autofirmado por cada usuario y sus llave privada y publica.
        Atributos:
             Nombre de usuario: debe ser unico por usuario, no menor a 3 caracters y no mayor a 50 caracters. 
        
    Actualizar: Permite actualizar los datos del usuario.
        Atributos:
             Nombre de usuario: debe ser unico por usuario, no menor a 3 caracters y no mayor a 50 caracters. 
    
    Obtener: Permite listar los usuarios regitrados y filtrar por el nombre de usuario, no es sensitivo a mayusculas o minusculas.
        Atributos:
            Identificaro unico por usuario, nombre de usuario.
            
    Eliminiar: Permite eliminar el usuario, su certificado, llave publica, llave privada y eliminar todos sus documentos firmados.

 
# Documentación de la API.
    Swagger: http://<Direccion IP>:8080/api/v1/swagger-ui/swagger-ui/index.html#/

    Archivo Postman : ApiRestDigitalSignature.postman_collection.json

# Manejo de errores:

    Se implementan diferentes códigos de error para identificar la causa del problema.
      - Se tiene un listado de codigos de error, ademas que se encapsulan los errores de logica de negocio 
      y se capturan los errores del sistema para no mostrar informacion que pueda ser usada de mala fe.
    Se proporcionan mensajes de error claros y descriptivos.

# Persistencia:
 
    Mysql se utiliza como base de datos.
 


