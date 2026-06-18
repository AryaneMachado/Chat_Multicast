# Chat Multicast

Aplicação de chat em Java que suporta três protocolos de comunicação: TCP, UDP (unicast) e UDP Multicast. Possui interface gráfica (Swing) e um cliente via linha de comando.

Projeto desenvolvido para a disciplina de Sistemas Distribuídos - IFSULDEMINAS Campus Machado.

## Funcionalidades

- Chat em grupo via UDP Multicast
- Cliente alternativo via terminal

## Estrutura do projeto

```
src/br/edu/ifsuldeminas/sd/chat/
├── ChatException.java          Exceção customizada
├── MessageContainer.java       Interface para receber mensagens
├── Sender.java                 Interface para envio de mensagens
├── Receiver.java                Interface para recebimento de mensagens
├── UDPSender.java / UDPReceiver.java
├── TCPSender.java / TCPReceiver.java
├── MulticastSender.java / MulticastReceiver.java
├── ChatFactory.java            Cria sender/receiver conforme o protocolo
├── client/                     Cliente via linha de comando
└── view/                       Interface gráfica (Swing)
    ├── ConnectionView.java     Tela de conexão
    └── ChatView.java           Tela do chat
```

## Como executar

Projeto Eclipse (sem Maven/Gradle). Para rodar:

1. Importe o projeto no Eclipse como "Existing Java Project"
2. Execute a classe `ConnectionView` para abrir a interface gráfica
3. Ou execute `Chat` (em `client/`) para usar via terminal

Para se comunicarem, é necessário rodar uma instância em cada ponta (ou duas instâncias na mesma máquina para teste local).

## Protocolos disponíveis

| Protocolo | Comunicação | Observações |
|---|---|---|
| TCP | Um para um | Conexão confiável, nova conexão a cada mensagem |
| UDP | Um para um | Sem garantia de entrega ou ordem |
| Multicast | Um para muitos | Todos os clientes do mesmo grupo recebem a mensagem |

## Sobre o modo Multicast

No modo Multicast, todos os clientes precisam usar o mesmo endereço de grupo e a mesma porta para se enxergarem. O endereço padrão sugerido na tela de conexão é `228.6.7.8`, mas qualquer endereço dentro da faixa `224.0.0.0` a `239.255.255.255` pode ser usado (a faixa `239.x.x.x` é a mais indicada para uso em redes locais).

Pontos importantes para o funcionamento:

- O endereço de grupo e a porta devem ser idênticos em todos os clientes
- O IP de cada máquina na rede não precisa ser configurado manualmente, é resolvido automaticamente
- Redes que isolam clientes entre si (hotspots de celular, algumas redes corporativas/Wi-Fi públicas) podem bloquear o tráfego multicast mesmo com a configuração correta. Nesses casos, prefira um roteador comum ou conexão via cabo

## Requisitos

- JDK 8 ou superior
- Todas as máquinas envolvidas devem estar na mesma rede local
