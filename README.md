# Roomify - Aplicativo de Reservas de Salas de Reunião

## Visão Geral do Projeto

O Roomify é um aplicativo mobile desenvolvido em **Kotlin** e **Android Studio** com o objetivo de otimizar o processo de reserva e gerenciamento de salas de reunião em ambientes corporativos. Ele busca resolver problemas comuns como a falta de clareza sobre a disponibilidade das salas e a dificuldade em gerenciar horários, proporcionando uma plataforma centralizada para agendamentos eficientes.

![](screenshots/login.png)  
_Tela de Login do aplicativo._

## Funcionalidades Principais

O sistema oferece uma série de funcionalidades para facilitar a gestão de salas:

* **Visualização de Disponibilidade:** Calendário interativo para visualizar a disponibilidade das salas em tempo real.
    ![](screenshots/calendario.png)  
    _Tela de Calendário para visualização de datas disponíveis._
* **Gestão de Reservas:**
    * Realização de novas reservas com confirmação automática por e-mail.
        ![](screenshots/confirmar_agendamento.png)  
        _Tela de confirmação de agendamento._
    * Alteração de reservas existentes (para administradores).
        ![](screenshots/gerenciar_salas.png)  
        _Tela de gerenciamento de salas (Administrador)._
    * Cancelamento de reservas, com atualização imediata da disponibilidade da sala.
* **Autenticação e Perfis de Acesso:** Sistema de login seguro com diferentes níveis de acesso para Usuários Comuns e Administradores.
    ![](screenshots/cadastro.png)  
    _Tela de Cadastro de novas contas._
* **Recuperação de Senha:** Funcionalidade para recuperação de senha via e-mail.
    ![](screenshots/recuperar_senha.png)  
    _Tela de Recuperação de Senha._
* **Relatórios Detalhados:** Geração e exportação de relatórios de uso das salas (filtrados por tipo de sala e exportáveis em PDF).
    ![](screenshots/gerar_relatorio.png)  
    _Tela de Geração de Relatórios._
    ![](screenshots/tela_admin.png)  
    _Visão geral da tela principal do administrador._
* **Gerenciamento Administrativo:**
    * Atualização de valores de aluguel das salas (para administradores).
        ![](screenshots/aluguel.png)  
        _Tela para alterar o valor do aluguel de salas._
    * Exclusão de reservas (para administradores).
* **Notificações:** Envio de e-mails sobre quaisquer mudanças ou reservas realizadas.

## Tecnologias Utilizadas

* **Linguagem de Programação:** Kotlin
* **Ambiente de Desenvolvimento:** Android Studio
* **Banco de Dados:** Firebase
* **Gerenciamento de Dependências:** Gradle
* **Design:** Material Design (padrão em apps Android modernos)

## Requisitos do Sistema

Para executar e desenvolver o Roomify, você precisará de:

* **Android Studio:** Versão mais recente recomendada.
* **SDK do Android:** Versão compatível com o projeto (verifique o `build.gradle` do módulo `app`).
* **JDK (Java Development Kit):** Versão compatível com o Android Studio.
* **Conexão com a Internet:** Necessária para o Gradle baixar dependências e para a comunicação com o Firebase.
* **Conta Firebase:** Configuração de um projeto Firebase para o banco de dados.

## Como Executar o Projeto

1.  **Clone o Repositório:**
    ```bash
    git clone [https://github.com/gustavovolpi/Roomify-Kotlin.git](https://github.com/gustavovolpi/Roomify-Kotlin.git)
    cd Roomify-Kotlin
    ```
2.  **Configuração do Firebase:**
    * Crie um novo projeto no [Firebase Console](https://console.firebase.google.com/).
    * Adicione um aplicativo Android ao seu projeto Firebase.
    * Siga as instruções do Firebase para baixar o arquivo `google-services.json` e coloque-o na pasta `app/` do seu projeto Android.
    * Configure as regras de segurança do seu banco de dados (Firestore ou Realtime Database, dependendo de qual você usou) no Firebase Console.
3.  **Abra no Android Studio:**
    * Abra o Android Studio.
    * Selecione "Open an existing Android Studio project" e navegue até a pasta raiz do projeto clonado (`Roomify-Kotlin`).
4.  **Sincronize o Gradle:**
    * Aguarde o Android Studio sincronizar o projeto com o Gradle. Se houver algum problema, verifique sua conexão com a internet e as dependências no `build.gradle`.
5.  **Execute o Aplicativo:**
    * Conecte um dispositivo Android (com depuração USB ativada) ou inicie um emulador.
    * Clique no botão "Run" (ícone de play verde) no Android Studio para instalar e executar o aplicativo.

## Estrutura do Projeto

A estrutura segue o padrão de projetos Android:

* `.gradle/`: Arquivos de configuração do Gradle.
* `.idea/`: Arquivos de configuração do Android Studio.
* `app/`: Contém o código-source do aplicativo, recursos, manifest, etc.
* `gradle/`: Wrappers do Gradle.
* `.gitignore`: Define quais arquivos e pastas o Git deve ignorar (já configurado para Android).
* `build.gradle.kts`: Arquivos de script Gradle para o projeto e módulos.
* `local.properties`: Propriedades locais do SDK (ignoradas pelo Git).
* `settings.gradle.kts`: Configurações de módulos do Gradle.
* `README.md`: Este arquivo de documentação.
* `LICENSE`: O arquivo de licença do projeto.
* `screenshot/`: Pasta para armazenar as imagens das telas do aplicativo.

## Mais Telas do Roomify

Para uma visão mais detalhada da interface do usuário e outras funcionalidades:

* ![](screenshots/perfil_usuario.png)  
    _Página de perfil do usuário._
* ![](screenshots/dominios_email.png)  
    _Configuração de domínios de email._
* ![](screenshots/editar_perfil.png)  
    _Tela de edição de perfil de usuário._
* ![](screenshots/editar_pergunta.png)  
    _Tela para editar uma pergunta frequente._
* ![](screenshots/favoritos.png)  
    _Projetos favoritos do usuário._
* ![](screenshots/filtros.png)  
    _Opções de filtragem de projetos._
* ![](screenshots/incluir_nova_pergunta.png)  
    _Formulário para incluir uma nova pergunta frequente._
* ![](screenshots/projeto_visualizacao.png)  
    _Visualização detalhada de um projeto._
* ![](screenshots/recuperar_senha.png)  
    _Tela de recuperação de senha._
* ![](screenshots/usuarios.png)  
    _Listagem de usuários._

---

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues para bugs ou sugestões de novas funcionalidades, ou enviar pull requests.

## Autores

* GUSTAVO VOLPI

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).