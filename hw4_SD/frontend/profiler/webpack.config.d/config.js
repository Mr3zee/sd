if (config.devServer) {
    config.devServer.proxy['/api/statistics/subscribe'] = {
        target: 'ws://localhost:8083', // profiler
        ws: true
    }
}
