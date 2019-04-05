const DEFAULT_OPTIONS = [[ '', '' ]];
const KEY =             'nlg-workspace/cell-options';


export const getCellOptions = workspace =>
    workspace[KEY] || DEFAULT_OPTIONS;

export const setCellOptions = ( workspace, cellNames ) =>
    workspace[KEY] =
        ( cellNames && cellNames.length )
            ? cellNames.map( name => [ name, name ])
            : DEFAULT_OPTIONS;
