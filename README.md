# EmployeeApp

Employee app é um aplicativo para gerenciamento de funcionários, este projeto foi construido para funcionar em conjunto com o Employee API -> https://github.com/nereuneto/EmployeeAPI.

## Instalação

Para instalar, é necessário abrir o projeto com o Android Studio e compilar o APK, após, executar no emulador ou dispositivo móvel.

## Configurações

Em build.graddle no módulo APP, alterar o exemplo abaixo:

debug {
            buildConfigField("String", "BASE_URL", "\"URL.DA.SUA:API/\"")
    }
    
Para que o app aponte para a api corretamente.
