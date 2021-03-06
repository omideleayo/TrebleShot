package com.genonbeta.TrebleShot.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.genonbeta.TrebleShot.GlideApp;
import com.genonbeta.TrebleShot.R;
import com.genonbeta.TrebleShot.object.Shareable;
import com.genonbeta.TrebleShot.util.FileUtils;
import com.genonbeta.TrebleShot.widget.EditableListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class ApplicationListAdapter
		extends EditableListAdapter<ApplicationListAdapter.PackageHolder, EditableListAdapter.EditableViewHolder>
{
	private SharedPreferences mPreferences;
	private PackageManager mManager;

	public ApplicationListAdapter(Context context, SharedPreferences preferences)
	{
		super(context);
		mPreferences = preferences;
		mManager = context.getPackageManager();
	}

	@Override
	public ArrayList<PackageHolder> onLoad()
	{
		ArrayList<PackageHolder> list = new ArrayList<>();

		for (PackageInfo packageInfo : mContext.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA)) {
			ApplicationInfo appInfo = packageInfo.applicationInfo;

			if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1 || mPreferences.getBoolean("show_system_apps", false))
				list.add(new PackageHolder(String.valueOf(appInfo.loadLabel(mManager)),
						appInfo,
						packageInfo.versionName,
						packageInfo.packageName,
						new File(appInfo.sourceDir)));
		}

		Collections.sort(list, getDefaultComparator());

		return list;
	}

	@NonNull
	@Override
	public EditableListAdapter.EditableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		return new EditableListAdapter.EditableViewHolder(getInflater().inflate(R.layout.list_application, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull final EditableListAdapter.EditableViewHolder holder, final int position)
	{
		final View parentView = holder.getView();
		final PackageHolder object = getItem(position);
		ImageView image = parentView.findViewById(R.id.image);
		TextView text1 = parentView.findViewById(R.id.text);
		TextView text2 = parentView.findViewById(R.id.text2);

		text1.setText(object.friendlyName);
		text2.setText(object.version);

		parentView.setSelected(object.isSelectableSelected());

		GlideApp.with(getContext())
				.load(object.appInfo)
				.override(160)
				.centerCrop()
				.into(image);
	}

	public static class PackageHolder extends Shareable
	{
		public static final String FORMAT = ".apk";
		public static final String MIME_TYPE = FileUtils.getFileContentType(FORMAT);

		public ApplicationInfo appInfo;
		public String version;
		public String packageName;

		public PackageHolder(String friendlyName, ApplicationInfo appInfo, String version, String packageName, File executableFile)
		{
			super(appInfo.uid,
					friendlyName,
					friendlyName + "_" + version + ".apk",
					MIME_TYPE,
					executableFile.lastModified(),
					executableFile.length(),
					Uri.fromFile(executableFile));

			this.appInfo = appInfo;
			this.version = version;
			this.packageName = packageName;
		}
	}
}
