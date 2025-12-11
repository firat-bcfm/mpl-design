# Jenkins Setup Guide

Docker Jenkins'te Firat ve Ozkan pipeline'larını kurulum rehberi.

## Hızlı Başlangıç

### 1. Otomatik Kurulum (Önerilen)

```bash
cd /Users/firat/Desktop/clean-mpl/mpl-master
./jenkins-setup/setup-jenkins-jobs.sh
```

Bu script:
- ✅ 4 job oluşturur (firat-dev, firat-prod, ozkan-dev, ozkan-prod)
- ✅ Jenkins'i otomatik reload eder
- ✅ Job'ları kullanıma hazır hale getirir

### 2. Shared Library Kurulumu

Jenkins'te shared library'yi aktifleştirmek için:

#### Yöntem A: Jenkins UI'dan (Önerilen)

1. Jenkins'i aç: http://localhost:8080
2. **Manage Jenkins** > **System**
3. **Global Pipeline Libraries** bölümüne in
4. **Add** butonuna tıkla

Ayarlar:
```
Name: mpl
Default version: main
☑ Load implicitly
☐ Allow default version to be overridden
☑ Include @Library changes in job recent changes

Retrieval method: Modern SCM
  Source Code Management: Git
    Project Repository: https://github.com/firat-bcfm/mpl-design.git
```

5. **Save** et

#### Yöntem B: Jenkins Script Console'dan

1. Jenkins > Manage Jenkins > Script Console
2. Aşağıdaki scripti yapıştır:

```groovy
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.libs.*
import jenkins.plugins.git.GitSCMSource

def jenkins = Jenkins.instance
def globalLibraries = jenkins.getDescriptor("org.jenkinsci.plugins.workflow.libs.GlobalLibraries")

def library = new LibraryConfiguration("mpl", new SCMSourceRetriever(
    new GitSCMSource(
        "mpl-library",
        "https://github.com/firat-bcfm/mpl-design.git",
        "",
        "*",
        "",
        false
    )
))
library.setDefaultVersion("main")
library.setImplicit(false)

globalLibraries.get().setLibraries([library])
jenkins.save()

println "✓ Shared library 'mpl' configured successfully!"
```

3. **Run** et

## Manuel Job Oluşturma

Otomatik script kullanmak istemiyorsan, manuel oluşturabilirsin:

### Firat-Dev Job

1. Jenkins ana sayfasında **New Item**
2. **Item name**: `firat-dev`
3. **Pipeline** seç, **OK**
4. **Configuration** sayfasında:

```
Description: Firat Development Pipeline - 6 Stage Deployment

☑ This project is parameterized
  - String Parameter
    Name: GIT_BRANCH
    Default Value: main
    Description: Git branch to build

Build Triggers:
  ☑ Poll SCM
    Schedule: H/5 * * * *

Pipeline:
  Definition: Pipeline script from SCM
  SCM: Git
    Repository URL: https://github.com/firat-bcfm/mpl-design.git
    Branches to build: */main
  Script Path: Jenkinsfile.firat-dev
  ☑ Lightweight checkout
```

5. **Save**

### Firat-Prod Job

1. Jenkins ana sayfasında **New Item**
2. **Item name**: `firat-prod`
3. **Pipeline** seç, **OK**
4. **Configuration**:

```
Description: Firat Production Pipeline - Manual Approval + Auto-Rollback

☑ This project is parameterized
  - String Parameter
    Name: GIT_BRANCH
    Default Value: main
    Description: Git branch to build

  - Boolean Parameter
    Name: SKIP_APPROVAL
    Default: false
    Description: Skip manual approval (use with caution)

Pipeline:
  Definition: Pipeline script from SCM
  SCM: Git
    Repository URL: https://github.com/firat-bcfm/mpl-design.git
    Branches to build: */main
  Script Path: Jenkinsfile.firat-prod
  ☑ Lightweight checkout
```

5. **Save**

### Ozkan Job'ları

Aynı adımları tekrarla, sadece:
- Job name: `ozkan-dev` / `ozkan-prod`
- Script Path: `Jenkinsfile.ozkan-dev` / `Jenkinsfile.ozkan-prod`

## Job'ları Test Etme

### 1. Firat-Dev Test

```bash
# Jenkins UI'dan
http://localhost:8080/job/firat-dev/build

# Veya curl ile
curl -X POST http://localhost:8080/job/firat-dev/build
```

### 2. Firat-Prod Test

```bash
# UI'dan (parametreli)
http://localhost:8080/job/firat-prod/build

# Build sırasında manuel approval gerekecek!
```

## Klasör Yapısı

```
jenkins-setup/
├── setup-jenkins-jobs.sh           # Otomatik kurulum scripti
├── shared-library-config.xml       # Shared library config (referans)
└── jobs/
    ├── firat-dev/
    │   └── config.xml              # Firat Dev job config
    ├── firat-prod/
    │   └── config.xml              # Firat Prod job config
    ├── ozkan-dev/
    │   └── config.xml              # Ozkan Dev job config
    └── ozkan-prod/
        └── config.xml              # Ozkan Prod job config
```

## Pipeline Özellikleri

### Firat-Dev
- ✅ 6 aşamalı pipeline
- ✅ Otomatik trigger (her 5 dakikada poll)
- ✅ Dev ortamına otomatik deploy
- ✅ Smoke tests
- 🔗 URL: http://localhost:8080/job/firat-dev

### Firat-Prod
- ✅ 6 aşamalı pipeline
- ✅ **Manuel approval** gerekli
- ✅ Otomatik backup
- ✅ **Auto-rollback** on failure
- ✅ Kapsamlı validation
- 🔗 URL: http://localhost:8080/job/firat-prod

### Ozkan-Dev & Ozkan-Prod
Firat ile aynı özellikler, sadece farklı configuration

## Troubleshooting

### Job'lar görünmüyor

```bash
# Jenkins'i restart et
docker restart jenkins

# Log'ları kontrol et
docker logs jenkins -f
```

### Shared Library hatası

```
Error: Library mpl not found
```

**Çözüm**: Shared Library konfigürasyonunu kontrol et (yukarıdaki adımları takip et)

### Git repo'ya erişemiyor

```bash
# Docker container'dan test et
docker exec jenkins git ls-remote https://github.com/firat-bcfm/mpl-design.git
```

### Permission denied

```bash
# Job klasörüne permission ver
docker exec jenkins chown -R jenkins:jenkins /var/jenkins_home/jobs
```

## Jenkins Container Yönetimi

```bash
# Container'ı başlat
docker start jenkins

# Container'ı durdur
docker stop jenkins

# Log'ları izle
docker logs jenkins -f

# Container içine gir
docker exec -it jenkins bash

# Jenkins'i restart et
docker restart jenkins
```

## Sonraki Adımlar

1. ✅ Job'ları oluştur (setup script ile)
2. ✅ Shared library'yi ayarla
3. 🔄 İlk build'i çalıştır
4. 🔧 Pipeline'ları ihtiyacına göre özelleştir

## Konfigürasyon Dosyaları

Tüm job'ların XML konfigürasyon dosyaları `jenkins-setup/jobs/` klasöründe.

İhtiyacın varsa bu dosyaları düzenleyip tekrar yükleyebilirsin:

```bash
# Job'ı güncelle
docker cp jenkins-setup/jobs/firat-dev/config.xml jenkins:/var/jenkins_home/jobs/firat-dev/config.xml
docker exec jenkins curl -X POST http://localhost:8080/reload
```

## GitHub Repo

Repo URL: https://github.com/firat-bcfm/mpl-design.git

Bu repo'da olması gerekenler:
- ✅ `resources/com/firat/pipeline/modules/` - Firat modülleri
- ✅ `resources/com/ozkan/pipeline/modules/` - Ozkan modülleri
- ✅ `vars/FiratDevPipeline.groovy` - Firat Dev wrapper
- ✅ `vars/FiratProdPipeline.groovy` - Firat Prod wrapper
- ✅ `vars/OzkanDevPipeline.groovy` - Ozkan Dev wrapper
- ✅ `vars/OzkanProdPipeline.groovy` - Ozkan Prod wrapper
- ✅ `Jenkinsfile.firat-dev`
- ✅ `Jenkinsfile.firat-prod`
- ✅ `Jenkinsfile.ozkan-dev`
- ✅ `Jenkinsfile.ozkan-prod`
