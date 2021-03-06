/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.data.yaml;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedHashMap;
import java.util.Map;

import io.amelia.data.ContainerWithValue;
import io.amelia.data.serialization.DataSerializable;
import io.amelia.data.serialization.DataSerialization;

public class YamlRepresenter extends Representer
{
	public YamlRepresenter()
	{
		this.multiRepresenters.put( ContainerWithValue.class, new RepresentConfigurationSection() );
		this.multiRepresenters.put( DataSerializable.class, new RepresentConfigurationSerializable() );
	}

	private class RepresentConfigurationSection extends RepresentMap
	{
		@Override
		public Node representData( Object data )
		{
			return super.representData( ( ( ContainerWithValue ) data ).values() );
		}
	}

	private class RepresentConfigurationSerializable extends RepresentMap
	{
		@Override
		public Node representData( Object data )
		{
			DataSerializable serializable = ( DataSerializable ) data;
			Map<String, Object> values = new LinkedHashMap<>();
			values.put( DataSerialization.SERIALIZED_TYPE_KEY, DataSerialization.getAlias( serializable.getClass() ) );
			values.putAll( serializable.serialize() );

			return super.representData( values );
		}
	}
}
