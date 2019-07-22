import { h }            from 'preact';
import renderToString   from 'preact-render-to-string';


export default ({ comment = null, fields = {}, mutation = {}, type }) =>
    renderToString(
        <xml>
            <block type={ type }>
                { Object.keys( fields ).map( name =>
                    <field key={ name } name={ name }>
                        { fields[name] }
                    </field>
                )}
                { mutation &&
                    <mutation { ...mutation } />
                }
                { comment &&
                    <comment>{ comment }</comment>
                }
            </block>
        </xml>
    );
