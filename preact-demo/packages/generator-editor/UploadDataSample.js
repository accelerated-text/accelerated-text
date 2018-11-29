import { h, Component } from 'preact';

import useStores        from '../context/use-stores';

export default useStores([
    'generatorEditor',
])( class UploadDataSample extends Component {

    onClick = () =>
        this.props.generatorEditor.onClickUpload({
            dataSample: 't-shirts.csv',
        });

    render() {
        const { dataSample } =  this.props.generatorEditor;

        return (
            <button onClick={ this.onClick }>{
                dataSample ? dataSample : 'upload'
            }</button>
        );
    }
});
