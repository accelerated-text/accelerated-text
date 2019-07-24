import { h }            from 'preact';


export default ({
    comment =   null,
    fields =    {},
    mutation =  {},
    type,
    values =    {},
}) =>
    <block type={ type }>
        { Object.keys( fields ).map( name =>
            <field key={ name } name={ name }>
                { fields[name] }
            </field>
        )}
        { Object.keys( values ).map( name =>
            <value key={ name } name={ name }>
                { values[name] }
            </value>
        )}
        { mutation &&
            <mutation { ...mutation } />
        }
        { comment &&
            <comment>{ comment }</comment>
        }
    </block>;
