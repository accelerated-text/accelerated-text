import os                   from 'os';


const concurrency = Math.abs(Math.floor(
    ( os.cpus().length - os.loadavg()[0]) / 2
)) || 1;


export default {
    concurrency,
    require:                [ 'esm' ],
    verbose:                true,
};
