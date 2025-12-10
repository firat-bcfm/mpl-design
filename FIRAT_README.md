# Firat Pipeline - Modular Deployment System

6 aşamalı modüler Jenkins deployment pipeline sistemi.

## Proje Yapısı

```
mpl-master/
├── resources/com/firat/pipeline/modules/
│   ├── Checkout/
│   │   ├── DevCheckout.groovy       # Step 1: Dev checkout
│   │   └── ProdCheckout.groovy      # Step 1: Prod checkout
│   ├── Build/
│   │   ├── DevBuild.groovy          # Step 2: Dev build
│   │   └── ProdBuild.groovy         # Step 2: Prod build
│   ├── Test/
│   │   ├── DevTest.groovy           # Step 3: Dev tests
│   │   └── ProdTest.groovy          # Step 3: Prod tests
│   ├── Deploy/
│   │   ├── DevDeploy.groovy         # Step 4: Dev deployment
│   │   └── ProdDeploy.groovy        # Step 4: Prod deployment
│   ├── SmokeTest/
│   │   ├── DevSmokeTest.groovy      # Step 5: Dev smoke tests
│   │   └── ProdSmokeTest.groovy     # Step 5: Prod smoke tests
│   └── PostDeployValidation/
│       ├── DevValidation.groovy     # Step 6: Dev validation
│       └── ProdValidation.groovy    # Step 6: Prod validation
├── vars/
│   ├── FiratDevPipeline.groovy      # Dev pipeline wrapper
│   └── FiratProdPipeline.groovy     # Prod pipeline wrapper
├── Jenkinsfile.firat-dev            # Dev pipeline örneği
└── Jenkinsfile.firat-prod           # Prod pipeline örneği
```

## Pipeline Aşamaları

### Firat-Dev (Development)

1. **Checkout** - Kaynak kod çekme
2. **Build** - Maven ile derleme
3. **Test** - Unit testler
4. **Deploy** - Dev ortamına deployment
5. **Smoke Test** - Temel endpoint kontrolleri
6. **Post-Deploy Validation** - Deployment doğrulama

### Firat-Prod (Production)

1. **Checkout** - Kaynak kod çekme + branch validasyonu
2. **Build** - Maven derleme + güvenlik kontrolleri
3. **Test** - Full test suite + code coverage
4. **Deploy** - Production deployment + backup + **MANUAL APPROVAL**
5. **Smoke Test** - Kapsamlı endpoint kontrolleri
6. **Post-Deploy Validation** - Full validation + **AUTO-ROLLBACK**

## Jenkins'te Kurulum

### 1. Shared Library Ekleme

Jenkins > Manage Jenkins > System > Global Pipeline Libraries:

```
Name: mpl
Default version: main
Retrieval method: Modern SCM
  - Git
  - Project Repository: /Users/firat/Desktop/clean-mpl/mpl-master
```

### 2. Jenkins Job Oluşturma

#### Firat-Dev Job

```
Job Name: firat-dev
Type: Pipeline
Pipeline Definition: Pipeline script from SCM
  SCM: Git
  Repository URL: /Users/firat/Desktop/clean-mpl/mpl-master
  Script Path: Jenkinsfile.firat-dev
```

#### Firat-Prod Job

```
Job Name: firat-prod
Type: Pipeline
Pipeline Definition: Pipeline script from SCM
  SCM: Git
  Repository URL: /Users/firat/Desktop/clean-mpl/mpl-master
  Script Path: Jenkinsfile.firat-prod
```

## Kullanım

### Jenkinsfile Örneği

```groovy
@Library('mpl') _

FiratDevPipeline {
    maven.tool_version = 'Maven 3'
    deploy.dev_host = 'dev.firat.local'
    deploy.dev_port = '8080'
    smoketest.endpoints = ['/health', '/api/status']
}
```

### Konfigürasyon Seçenekleri

#### Maven Ayarları
```groovy
maven.tool_version = 'Maven 3'        // Jenkins'te tanımlı Maven versiyonu
maven.settings_path = '/path/to/settings.xml'  // İsteğe bağlı
```

#### Deployment Ayarları (Dev)
```groovy
deploy.dev_host = 'dev.firat.local'
deploy.dev_port = '8080'
deploy.ssh_enabled = true
deploy.ssh_user = 'deploy'
deploy.path = '/opt/firat-dev'
deploy.stop_command = 'systemctl stop firat-dev'
deploy.start_command = 'systemctl start firat-dev'
```

#### Deployment Ayarları (Prod)
```groovy
deploy.prod_host = 'prod.firat.com'
deploy.prod_port = '8080'
deploy.backup_path = '/opt/backups/firat-prod'
deploy.auto_rollback = true
deploy.ssh_enabled = true
```

#### Smoke Test Ayarları
```groovy
smoketest.endpoints = ['/health', '/api/status', '/api/info']
smoketest.max_retries = 10
smoketest.retry_delay = 5  // saniye
```

#### Validation Ayarları (Prod)
```groovy
validation.check_database = true
validation.check_dependencies = true
validation.check_logs = true
validation.check_metrics = true
validation.auto_rollback = true
```

## Özellikler

### Development Pipeline
- ✓ Hızlı deployment
- ✓ Otomatik çalışma
- ✓ Temel smoke testler
- ✓ Log kontrolleri

### Production Pipeline
- ✓ **Manuel approval** deployment öncesi
- ✓ **Otomatik backup** her deployment'ta
- ✓ **Auto-rollback** hata durumunda
- ✓ Kapsamlı smoke testler
- ✓ Full validation
- ✓ Güvenlik kontrolleri
- ✓ Code coverage raporları
- ✓ Branch validasyonu

## Jenkins'ten Çalıştırma

### UI'dan
```
Jenkins > firat-dev > Build Now
Jenkins > firat-prod > Build with Parameters
```

### CLI'dan
```bash
# Dev deployment
java -jar jenkins-cli.jar -s http://localhost:8080/ build firat-dev

# Prod deployment
java -jar jenkins-cli.jar -s http://localhost:8080/ build firat-prod
```

### API'dan
```bash
# Dev
curl -X POST http://localhost:8080/job/firat-dev/build

# Prod
curl -X POST http://localhost:8080/job/firat-prod/build
```

## Rollback Prosedürü

### Otomatik Rollback
Production deployment'ta validation başarısız olursa otomatik rollback yapılır.

### Manuel Rollback
```bash
# SSH ile prod sunucuya bağlan
ssh deploy@prod.firat.com

# Backup'tan geri yükle
sudo systemctl stop firat-prod
sudo cp /opt/backups/firat-prod/app_YYYYMMDD_HHMMSS.jar /opt/firat-prod/app.jar
sudo systemctl start firat-prod

# Kontrol et
curl https://prod.firat.com/health
```

## Troubleshooting

### Build Hataları
```bash
# Maven bağımlılık problemleri
mvn dependency:tree

# Test hataları
mvn test -X
```

### Deployment Hataları
```bash
# SSH bağlantısı
ssh deploy@dev.firat.local "ls -la /opt/firat-dev"

# Uygulama durumu
ssh deploy@dev.firat.local "systemctl status firat-dev"
```

### Smoke Test Hataları
```bash
# Manuel endpoint testi
curl -v http://dev.firat.local:8080/health

# Log kontrolü
ssh deploy@dev.firat.local "tail -f /var/log/firat-dev/app.log"
```

## Gereksinimler

- Jenkins 2.x+
- Maven 3.6+
- JDK 11+
- Git
- SSH access (deployment için)

## Katkıda Bulunma

Pipeline'ları özelleştirmek için:

1. `resources/com/firat/pipeline/modules/` altındaki groovy dosyalarını düzenle
2. `vars/FiratDevPipeline.groovy` veya `vars/FiratProdPipeline.groovy` güncelle
3. Test et ve commit et

## Destek

Sorular için:
- Jenkins logs: `Jenkins > firat-dev/firat-prod > Console Output`
- Pipeline syntax: Jenkins > Pipeline Syntax
