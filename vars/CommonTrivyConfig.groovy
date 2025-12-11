/**
 * Common Trivy Configurations
 * Reusable preset configurations for different scenarios
 */

/**
 * Get Trivy config for FIRAT DEV environment
 */
def firatDev() {
    return [
        enabled: true,
        scanType: 'fs',
        severity: 'HIGH,CRITICAL',
        exitCode: '0',
        format: 'table'
    ]
}

/**
 * Get Trivy config for FIRAT PROD environment
 */
def firatProd() {
    return [
        enabled: true,
        scanType: 'image',
        severity: 'CRITICAL',
        exitCode: '1',  // Fail on CRITICAL in production!
        format: 'sarif'
    ]
}

/**
 * Get Trivy config for OZKAN DEV environment
 */
def ozkanDev() {
    return [
        enabled: true,
        scanType: 'image',
        severity: 'MEDIUM,HIGH,CRITICAL',
        exitCode: '0',
        format: 'json'
    ]
}

/**
 * Get Trivy config for OZKAN PROD environment
 */
def ozkanProd() {
    return [
        enabled: true,
        scanType: 'image',
        severity: 'HIGH,CRITICAL',
        exitCode: '1',  // Fail on findings in production!
        format: 'json'
    ]
}

/**
 * Disabled config (for when you want to skip Trivy)
 */
def disabled() {
    return [
        enabled: false,
        scanType: 'fs',
        severity: 'HIGH,CRITICAL',
        exitCode: '0',
        format: 'table'
    ]
}

/**
 * Custom config - merge your settings with defaults
 */
def custom(Map userConfig) {
    def defaults = [
        enabled: false,
        scanType: 'fs',
        severity: 'HIGH,CRITICAL',
        exitCode: '0',
        format: 'table'
    ]
    return defaults + userConfig
}
