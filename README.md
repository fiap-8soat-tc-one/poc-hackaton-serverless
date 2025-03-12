# 🚀 POC az-fiap-hackathon

## ✅ Pré-requisitos

Antes de iniciar, certifique-se de que possui as seguintes ferramentas instaladas:

- 🏗️ [Azure Functions Tools para desenvolvimento local](https://learn.microsoft.com/en-us/azure/azure-functions/functions-run-local?tabs=windows%2Cisolated-process%2Cnode-v4%2Cpython-v2%2Chttp-trigger%2Ccontainer-apps&pivots=programming-language-java)
- 🐳 [Docker](https://www.docker.com/)
- ☸️ [Kubernetes](https://kubernetes.io/)
- ☕ [Java 17+](https://openjdk.org/projects/jdk/17/) / [Maven](https://maven.apache.org/)
- 🛢️ [MySQL](https://www.mysql.com/) instalado localmente ou via container
- 📩 [RabbitMQ](https://www.rabbitmq.com/) instalado localmente ou via container
- 🚀 [Intellij](https://www.jetbrains.com/pt-br/idea/download/?section=windows) Como IDE recomendada e a instalação do plugin [Azure Toolkit Intellij](https://plugins.jetbrains.com/plugin/8053-azure-toolkit-for-intellij)

## Execução Local

1. Para execução local, primeiro criar o seguinte arquivo na raiz do projeto:

   - **🔹 Nome do arquivo:** `local.settings.json`
   - **🔹 Conteúdo:** 
```json
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "UseDevelopmentStorage=true",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "MySqlConnectionString": "Server=localhost;Port=30081;Database=test;Uid=test_user;Pwd=test_password;",
    "RabbitMqConnectionString": "amqp://admin:admin@localhost:30088/",
    "RabbitMqOutQueueName": "outputQueue",
    "SendGridApiKey": "",
    "NotifyQueue": "notifyQueue"
  }
}

```

## 🛠️ Configuração do MySQL

1. Configure o usuário/senha do banco na `connectionString`:
    - **🔹 Para execução local**: defina dentro do arquivo `local.settings.json`.
    - **🔹 Para execução no Kubernetes**: defina dentro do arquivo `kubernetes/infra/secrets`.

2. Utilize a interface gráfica de sua preferência e execute o script localizado em `scripts/mysql.sql` para criar o banco de dados e a tabela.

3. Para configurar o MySQL dentro do cluster Kubernetes local, utilize o seguinte comando **Helm**:

   ```sh
   helm install mysql-demo bitnami/mysql --namespace fiap-hackathon \
     --set auth.rootPassword=rootpassword \
     --set auth.database=test \
     --set auth.username=test_user \
     --set auth.password=test_password
   ```

   **⚠️ Nota:** A *secret* versionada neste repositório já contempla a `connectionString` correspondente a essa configuração. Qualquer alteração precisa ser revalidada.

4. Se o cluster local for gerado pelo script KIND (`kubernetes/infra/kc.yaml`), exponha o MySQL na porta `30081` com o seguinte comando:

   ```sh
   kubectl patch svc mysql-demo -p '{"spec": {"type": "NodePort", "ports": [{"port": 3306, "nodePort": 30081}]}}'
   ```

## 📨 Configuração do RabbitMQ

1. Crie duas filas dentro do RabbitMQ:
    - 📌 `notifyQueue` (para notificações)
    - 📌 `outputQueue` (para processamento)

   Caso os nomes das filas sejam diferentes, atualize os arquivos:
    - **📍 Para execução local**: `local.settings.json`
    - **📍 Para execução no Kubernetes**: manifestos dentro de `kubernetes/funcs`

2. Para configurar o RabbitMQ dentro do cluster Kubernetes local, utilize o seguinte comando **Helm**:

   ```sh
   helm install rabbitmq bitnami/rabbitmq --namespace fiap-hackathon --create-namespace \
     --set auth.username=admin --set auth.password=admin --set persistence.enabled=false
   ```

   **⚠️ Nota:** A *secret* versionada neste repositório já contempla a `connectionString` correspondente a essa configuração. Qualquer alteração precisa ser revalidada.

3. Se o cluster local for gerado pelo script KIND (`kubernetes/infra/kc.yaml`), exponha o RabbitMQ nas portas `30088` (broker) e `30086` (portal de gerenciamento) com o seguinte comando:

   ```sh
   kubectl patch svc rabbitmq -n fiap-hackathon -p '{"spec": {"type": "NodePort", "ports": [{"port": 5672, "nodePort": 30088}, {"port": 15672, "nodePort": 30086}]}}'
   ```

## ☸️ Configuração do Cluster Kubernetes Local

Utilize a tecnologia que melhor lhe convier, como **K3s**, **KIND** ou **MicroK8s**.

### 🔹 Recomendações
Minha recomendação é utilizar o [KIND](https://kind.sigs.k8s.io/docs/user/quick-start/#installation). Caso opte por ele, será necessário instalar o **metric-server**:

```sh
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
kubectl patch deployment metrics-server -n kube-system --type='json' -p='[{"op": "add", "path": "/spec/template/spec/containers/0/args/-", "value": "--kubelet-insecure-tls"}]'
```

### ⚡ Instalação do KEDA

```sh
helm repo add kedacore https://kedacore.github.io/charts
helm repo update
helm install keda kedacore/keda --namespace keda --create-namespace
```

## 🏗️ Execução do Ambiente Kubernetes

Siga os passos abaixo para configurar e executar o ambiente Kubernetes:

1️⃣ **Criar o Cluster Kubernetes**

2️⃣ **Configurar o metric-server** (caso ainda não esteja configurado)

3️⃣ **Configurar o KEDA**

4️⃣ **Instalar o MySQL via Helm**

5️⃣ **Instalar o RabbitMQ via Helm**

6️⃣ **Criar o namespace `fiap-hackathon`** (caso ainda não esteja criado)

7️⃣ **Executar o arquivo de *secrets*** do diretório `kubernetes/infra/secrets.yaml`

8️⃣ **Executar os manifestos do diretório `kubernetes/funcs/*.yaml`**

- A ordem de execução dos arquivos dentro de `kubernetes/funcs/` não é relevante, pois cada arquivo é independente.


