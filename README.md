# Chat Multicast

Aplicação de chat em Java que suporta três protocolos de comunicação: TCP, UDP (unicast) e UDP Multicast. Possui interface gráfica (Swing) e um cliente via linha de comando. Mas a versão desse trabalho é focado na comunocação via UDP Multicast.

Projeto desenvolvido para a disciplina de Sistemas Distribuídos - IFSULDEMINAS Campus Machado.

## Funcionalidades (Foco da Entrega)

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

Projeto Eclipse (sem Maven/Gradle).

### Importando o projeto

1. Abra o Eclipse
2. Vá em `File > Import...`
3. Selecione `General > Existing Projects into Workspace` e clique em `Next`
4. Em "Select root directory", clique em `Browse...` e selecione a pasta `tcp-udp-chat`
5. Marque o projeto na lista e clique em `Finish`

### Executando a interface gráfica

1. No `Package Explorer`, navegue até `src > br.edu.ifsuldeminas.sd.chat.view`
2. Clique com o botão direito na classe `ConnectionView`
3. Selecione `Run As > Java Application`
4. Na tela que abrir, escolha o protocolo (UDP, TCP ou Multicast), preencha os campos e clique em `Entrar no Chat`

Para testar a comunicação entre dois usuários, repita os passos acima em uma segunda execução (`Run As > Java Application` novamente), seja na mesma máquina ou em uma máquina diferente na mesma rede.

### Executando o cliente via terminal

1. No `Package Explorer`, navegue até `src > br.edu.ifsuldeminas.sd.chat.client`
2. Clique com o botão direito na classe `Chat`
3. Selecione `Run As > Java Application`
4. Siga as instruções exibidas no console para escolher protocolo, portas e nickname

### Configurando portas e endereços

- **Multicast**: todos os clientes precisam usar o mesmo endereço de grupo e a mesma porta (ver seção abaixo)

## Protocolos disponíveis

| Protocolo | Comunicação | Observações |
|---|---|---|
| Multicast | Um para muitos | Todos os clientes do mesmo grupo recebem a mensagem |

## Sobre o modo Multicast (Foco da Entrega)

No modo Multicast, todos os clientes precisam usar o mesmo endereço de grupo e a mesma porta para se enxergarem. O endereço padrão sugerido na tela de conexão é `228.6.7.8`, mas qualquer endereço dentro da faixa `224.0.0.0` a `239.255.255.255` pode ser usado (a faixa `239.x.x.x` é a mais indicada para uso em redes locais).

Pontos importantes para o funcionamento:

- O endereço de grupo e a porta devem ser idênticos em todos os clientes
- O IP de cada máquina na rede não precisa ser configurado manualmente, é resolvido automaticamente
- Redes que isolam clientes entre si (hotspots de celular, algumas redes corporativas/Wi-Fi públicas) podem bloquear o tráfego multicast mesmo com a configuração correta. Nesses casos, prefira um roteador comum ou conexão via cabo

## Requisitos

- JDK 8 ou superior
- Todas as máquinas envolvidas devem estar na mesma rede local
