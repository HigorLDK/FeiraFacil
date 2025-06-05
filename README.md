# 🛒 Feira Fácil

**Feira Fácil** é um aplicativo Android desenvolvido em **Kotlin** que tem como objetivo facilitar o controle de compras em feiras e mercados. Com ele, o usuário pode adicionar, atualizar e acompanhar os itens da sua feira, com controle de quantidade, preço unitário e valor total, tudo salvo em tempo real na nuvem com **Firebase**.

## 📱 Funcionalidades

- Criação de listas de feira personalizadas
- Adição de itens com nome, categoria, valor e quantidade
- Incremento e decremento da quantidade de cada item
- Cálculo automático do valor total da compra
- Atualização de preço por item com formatação monetária
- Armazenamento em nuvem com Firebase Firestore
- Interface moderna e simples de usar
- Organização dos dados com **arquitetura MVVM**

## 🧱 Arquitetura

O projeto segue o padrão **MVVM (Model - View - ViewModel)** com separação clara entre camadas:

- `Model`: Representações de dados (como a classe `Lista`)
- `ViewModel`: Lógica de interface desacoplada da UI (LiveData, estados de carregamento, etc.)
- `Repository`: Camada de acesso aos dados no Firestore
- `View`: Activities e Fragments observando dados e atualizando a interface

## ☁️ Firebase Utilizado

- **Firebase Authentication**: Gerenciamento de usuários (login e cadastro)
- **Firebase Firestore**: Armazenamento de dados em nuvem (listas e itens)
- **Firebase Extensions**: Uso de `Source.SERVER` e `Source.CACHE` para melhorar a experiência offline

## 🛠 Tecnologias

- Kotlin
- Android SDK (Jetpack)
- ViewModel & LiveData
- Firebase (Auth + Firestore)
- Material Design (Bottom Sheets, Snackbars, TextInputLayouts)
- Coroutines para chamadas assíncronas

## 📂 Estrutura de Pastas (Simplificada)
