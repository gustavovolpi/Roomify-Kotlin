# Roomify - Aplicativo de Reservas de Salas de Reunião

## Visão Geral do Projeto

[cite_start]O Roomify é um aplicativo mobile desenvolvido em **Kotlin** e **Android Studio** com o objetivo de otimizar o processo de reserva e gerenciamento de salas de reunião em ambientes corporativos[cite: 4, 5]. [cite_start]Ele busca resolver problemas comuns como a falta de clareza sobre a disponibilidade das salas e a dificuldade em gerenciar horários, proporcionando uma plataforma centralizada para agendamentos eficientes[cite: 5, 6].

![](screenshots/tela_admin.png)
_Visão geral da tela principal do administrador._

## Funcionalidades Principais

O sistema oferece uma série de funcionalidades para facilitar a gestão de salas:

* [cite_start]**Visualização de Disponibilidade:** Calendário interativo para visualizar a disponibilidade das salas em tempo real[cite: 10, 48].
    * ![](screenshots/calendario.png)
        _Tela de Calendário para visualização de datas disponíveis._
* **Gestão de Reservas:**
    * [cite_start]Realização de novas reservas com confirmação automática por e-mail[cite: 36, 51].
        * ![](screenshots/confirmar_agendamento.png)
            _Tela de confirmação de agendamento._
    * [cite_start]Alteração de reservas existentes (para administradores)[cite: 62].
        * ![](screenshots/gerenciar_salas.png)
            _Tela de gerenciamento de salas (Administrador)._
    * [cite_start]Cancelamento de reservas, com atualização imediata da disponibilidade da sala[cite: 22].
* [cite_start]**Autenticação e Perfis de Acesso:** Sistema de login seguro com diferentes níveis de acesso para Usuários Comuns e Administradores[cite: 23, 24, 50].
    * ![](screenshots/login.png)
        _Tela de Login do aplicativo._
    * ![](screenshots/cadastro.png)
        _Tela de Cadastro de novas contas._
* [cite_start]**Recuperação de Senha:** Funcionalidade para recuperação de senha via e-mail[cite: 51, 52, 53, 54].
    * ![](screenshots/recuperar_senha.png)
        _Tela de Recuperação de Senha._
* [cite_start]**Relatórios Detalhados:** Geração e exportação de relatórios de uso das salas (filtrados por tipo de sala e exportáveis em PDF)[cite: 22, 41, 45, 107, 108].
    * ![](screenshots/gerar_relatorio.png)
        _Tela de Geração de Relatórios._
* **Gerenciamento Administrativo:**
    * [cite_start]Atualização de valores de aluguel das salas (para administradores)[cite: 55, 56, 57, 109, 110, 111, 112].
        * ![](screenshots/aluguel.png)
            _Tela para alterar o valor do aluguel de salas._
    * [cite_start]Exclusão de reservas (para administradores)[cite: 68, 70, 71, 72].
* [cite_start]**Notificações:** Envio de e-mails sobre quaisquer mudanças ou reservas realizadas[cite: 24, 51, 66, 67].

## Tecnologias Utilizadas

* [cite_start]**Linguagem de Programação:** Kotlin [cite: 14, 16]
* [cite_start]**Ambiente de Desenvolvimento:** Android Studio [cite: 14, 15]
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
    git clone [https://github.com/gustavovolpi/Roomify-Android.git](https://github.com/gustavovolpi/Roomify-Android.git) # Substitua pela URL do seu repositório
    cd Roomify-Android
    ```
2.  **Configuração do Firebase:**
    * Crie um novo projeto no [Firebase Console](https://console.firebase.google.com/).
    * Adicione um aplicativo Android ao seu projeto Firebase.
    * Siga as instruções do Firebase para baixar o arquivo `google-services.json` e coloque-o na pasta `app/` do seu projeto Android.
    * Configure as regras de segurança do seu banco de dados (Firestore ou Realtime Database, dependendo de qual você usou) no Firebase Console.
3.  **Abra no Android Studio:**
    * Abra o Android Studio.
    * Selecione "Open an existing Android Studio project" e navegue até a pasta raiz do projeto clonado (`Roomify-Android`).
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
* `screenshots/`: Pasta para armazenar as imagens das telas do aplicativo.

## Mais Telas do Roomify

Para uma visão mais detalhada da interface do usuário e outras funcionalidades:

* ![](screenshots/perfil_usuario.png)
    _Página de perfil do usuário._
* ![](screenshots/tela_admin.png)
    _Tela principal do Administrador._
* ![](screenshots/gerenciar_salas.png)
    _Tela de gerenciamento de salas (Administrador)._

---

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues para bugs ou sugestões de novas funcionalidades, ou enviar pull requests.

## Autores

* [cite_start]GUSTAVO VOLPI [cite: 1]

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).