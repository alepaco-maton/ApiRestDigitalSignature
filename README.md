# API para Firmar Documentos Digitales

# Descripción:

Esta API RESTful permite la gestion de usuarios y que cada usuario pueda firmar sus documentos con certificado digital.

# Funcionalidades:

<img width="1423" alt="imagen" src="https://github.com/user-attachments/assets/4b747f14-ab72-4cd8-b762-a0e1ad07aa20">

## Gestion de Usuario

### Crear: 
Permite crear un usuario, adicionalmente a esto se crea un certificado autofirmado por cada usuario y sus llave privada y publica.
#### Atributos: 
            userName: el nombre de usuario debe ser unico por usuario, no menor a 3 caracters y no mayor a 50 caracters. 

### Actualizar: 
Permite actualizar los datos del usuario.
#### Atributos: 
            userName: el nombre de usuario debe ser unico por usuario, no menor a 3 caracters y no mayor a 50 caracters. 

### Obtener: 
Permite listar los usuarios regitrados y filtrar por el nombre de usuario, no es sensitivo a mayusculas o minusculas.
#### Atributos: 
            Id: Identificaro unico por usuario
            userName: Nombre de usuario.
            
### Eliminiar: 
Permite eliminar el usuario, su certificado, llave publica, llave privada y eliminar todos sus documentos firmados.
#### Atributos: 
            Id: Identificaro unico por usuario

## Gestion de Documentos

### Firmar Documento (Solo documentos DPF): 
Permite firmar un documento del tipo PDF con el certificado del usuario.
#### Atributos: 
            userId: identificador unico del usuario.
            file: documento PDF a firmar.

### Descargar documento: 
Permite descargar el documento firmado por el usuario.
#### Atributos: 
            id: identificador unico del documento.
            
### Obtener informacion del documento (Solo documentos DPF):
Permite obtener la informacaion de la firma del documento.
#### Atributos: 
            file: documento PDF del que se quiere obtener la informacion .
   
# Requisitos 
- Java 17+
- Maven 3.9.9+
- Mysql 8.0.36+
- Spring boot 3.3.3
- Postman Version 7.36.7+

# Configuracion del archivo application.properties para el despliegue de la aplicacion

## Conexion a la base de datos
     Se requiere q configure sus credenciales para poder acceder a la base de datos y la direccion IP, en el archivo: src/main/resources/application.properties

            spring.datasource.url = jdbc:mysql://<IP>:3306/digitalsignature?createDatabaseIfNotExist=true
            spring.datasource.username = <usuario>
            spring.datasource.password = <passwrod>

    Esta configuracion crea automaticamente el schema de base de datos "digitalsignature" y tambien esta configuracion: 
            
            spring.jpa.hibernate.ddl-auto = update

    se encarga de crear las tablas de base de datos de manera automatica. 

## Configuracion de donde se almacenaran los documentos por cada usuario 
            path.folder.by.user=./filesByUser

## Configuracion del puerto que se usara para que el servcio este disponible
            server.port = 8080

# Construccion y ejecucion del servicio
Ejecutar los siguientes comandos
            
            mvn clean package      
            
            mvn spring-boot:run

  
# Documentación de la API.
## Swagger
http://<Direccion_IP>:8080/api/v1/swagger-ui/swagger-ui/index.html#/

## Postman 
[ApiRestDigitalSignature.postman_collection.json
](https://github.com/alepaco-maton/ApiRestDigitalSignature/blob/master/ApiRestDigitalSignature.postman_collection.json)

